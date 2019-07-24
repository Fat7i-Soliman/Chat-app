package com.example.howudoing


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainFragment : Fragment() {

    private lateinit var demoCollectionPagerAdapter: DemoCollectionPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var tab: TabLayout
    private lateinit var mAuth :FirebaseAuth
    private lateinit var ref : DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mAuth = FirebaseAuth.getInstance()
        ref= FirebaseDatabase.getInstance().reference.child("Users")
        demoCollectionPagerAdapter = DemoCollectionPagerAdapter(childFragmentManager)
        tab= view.findViewById(R.id.tab_id)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = demoCollectionPagerAdapter
        tab.setupWithViewPager(viewPager)
        setHasOptionsMenu(true)
    }


    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser==null){
            SendToStart()
        }else{
            val user = mAuth.currentUser!!.uid
            ref.child(user).child("online").setValue(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.log_out) {
            val user = mAuth.currentUser!!.uid
            ref.child(user).child("online").setValue(false)

            FirebaseAuth.getInstance().signOut()
            SendToStart()

        }else if (item?.itemId == R.id.setting){
            this.findNavController().navigate(R.id.action_mainFragment_to_settingFragment)
        }else if (item?.itemId==R.id.all_users){
            this.findNavController().navigate(R.id.action_mainFragment_to_usersFragment)

        }
        return super.onOptionsItemSelected(item)
    }

    private fun SendToStart(){
        this.findNavController().navigate(R.id.action_mainFragment_to_welcomeFragment)
    }
}


class DemoCollectionPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm){
    override fun getItem(p0: Int): Fragment? {
        return when(p0){
            0->  Requests()
            1->  Chats()
            2->  Friends()
            else ->  null
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
       return when(position){
            0->  "Requests"
            1->  "Chats"
            2->  "Friends"
            else ->  null
        }
    }
}
