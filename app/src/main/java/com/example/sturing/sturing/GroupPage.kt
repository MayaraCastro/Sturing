package com.example.sturing.sturing

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_group_page.*
import kotlinx.android.synthetic.main.fragment_group_page.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [GroupPage.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [GroupPage.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class GroupPage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var itemSelecionado: Int? = null
    private var groupSelecionado: String? = null
    private var groupName: String? = null
    private var groupDescription: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val bundle = arguments
        groupSelecionado = bundle!!.getString("group")
        itemSelecionado = bundle!!.getInt("tab")
        groupName = bundle!!.getString("name")
        groupDescription = bundle!!.getString("description")

        if (groupSelecionado == null) {
            Log.d("GROUP SELEICONADOO", "NULLO")
        }
        else{
            Log.d("GROUP SELEICONADOO", groupSelecionado.toString())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("item", itemSelecionado!!)
        outState.putString("group", groupSelecionado)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_group_page, container, false)
        setHasOptionsMenu(true)

        val viewPager = view.findViewById(R.id.viewpager) as ViewPager

        setupViewPager(viewPager)
        viewPager.isNestedScrollingEnabled = true

        if(savedInstanceState !=null){
            itemSelecionado = savedInstanceState!!.getInt("item")
        }

        viewPager.currentItem = itemSelecionado!!

        val tabLayout = view.findViewById(R.id.tabs) as TabLayout
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#008577"))
        tabLayout.setTabTextColors(Color.parseColor("#7fa87f"), Color.parseColor("#008577"))
        assert(viewPager != null)
        tabLayout.setupWithViewPager(viewPager)

        view.btAddUser.setOnClickListener {
            addUserToGroup()
        }

        view.txtName.text = groupName
        view.txtDescription.text = groupDescription

        return view
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = Adapter(childFragmentManager)

        val mFragGroupQuestions = FragmentGroupQuestions()
        val mFragFlashCards = FragmentFlashCards()
        val bundle = Bundle()
        bundle.putString("group", groupSelecionado)   //parameters are (key, value).
        mFragGroupQuestions.arguments = bundle  //set the group
        mFragFlashCards.arguments = bundle

        adapter.addFragment(HomeFragment(), getString(R.string.home))
        adapter.addFragment(mFragGroupQuestions, getString(R.string.questions))
        adapter.addFragment(mFragFlashCards, getString(R.string.flash_cards))
        viewPager.adapter = adapter

    }

    internal class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val mFragments = ArrayList<Fragment>()
        private val mFragmentTitles = ArrayList<String>()

        fun addFragment(fragment: Fragment, title: String) {
            mFragments.add(fragment)
            mFragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments.get(position)
        }

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitles.get(position)
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GroupPage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                GroupPage().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    fun addUserToGroup(){
        val i = Intent(activity, FindAddUser::class.java)
        i.putExtra("group", groupSelecionado)
        i.putExtra("funcao", 1)//mostrar só amigos
        startActivity(i)
    }
}
