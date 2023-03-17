package apps.hadjou.ilyas.sunhopeapp

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import apps.hadjou.ilyas.sunhopeapp.Fragment.ChatFragment
import apps.hadjou.ilyas.sunhopeapp.Fragment.EditFragment
import apps.hadjou.ilyas.sunhopeapp.Fragment.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
class MainActivity : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener {

      // instance of 3 fragment
      private  val idFragment= EditFragment()
      private  val chatFragment= ChatFragment()
      private  val homeFragment= HomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         // initially fragment and item Menu selected
        setFragment(homeFragment)
        navViewEnd.selectedItemId=R.id.navigation_home

        // changed clicked item menu listener
        navViewEnd.setOnNavigationItemSelectedListener(this@MainActivity)
    }



    //Function..

    //Function applicable when
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {

            R.id.navigation_chat -> {
                setFragment(chatFragment)
                return true
            }
            R.id.navigation_home -> {
                 setFragment(homeFragment)
                return true
            }
            R.id.navigation_id -> {
                setFragment(idFragment)
                return true
            }
            else -> return false
        }
    }

    //function of set Fragment exist
    private fun setFragment(fragment: Fragment) {
        val fr=supportFragmentManager.beginTransaction()
        fr.replace(R.id.cordinatView,fragment)
        fr.commit()

    }


}
