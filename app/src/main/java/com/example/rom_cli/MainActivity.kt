package com.example.rom_cli

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val bottomNavDestinations = setOf(
        R.id.homeFragment,
        R.id.aboutFragment,
        R.id.settingsFragment,
    )

    private val homeFlowDestinations = setOf(
        R.id.homeFragment,
        R.id.analysisFragment,
        R.id.shoulderAnalysisFragment,
        R.id.poseIntroductionFragment,
        R.id.statisticsFragment,
        R.id.romHistoryFragment,
        R.id.romResultsFragment,
        R.id.profileFragment,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHost = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHost.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId !in bottomNavDestinations) return@setOnItemSelectedListener false

            when (item.itemId) {
                R.id.homeFragment -> {
                    if (!navController.popBackStack(R.id.homeFragment, false)) {
                        navController.navigate(R.id.homeFragment)
                    }
                }
                else -> {
                    navController.navigate(
                        item.itemId,
                        null,
                        navOptions {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    )
                }
            }
            true
        }

        bottomNav.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> navController.popBackStack(R.id.homeFragment, false)
                R.id.aboutFragment -> navController.popBackStack(R.id.aboutFragment, false)
                R.id.settingsFragment -> navController.popBackStack(R.id.settingsFragment, false)
                else -> Unit
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNav.visibility = if (destination.id == R.id.cameraFragment) {
                View.GONE
            } else {
                View.VISIBLE
            }

            val selectedMenuId = when (destination.id) {
                in homeFlowDestinations -> R.id.homeFragment
                R.id.aboutFragment -> R.id.aboutFragment
                R.id.settingsFragment -> R.id.settingsFragment
                else -> null
            }
            selectedMenuId?.let { id ->
                bottomNav.menu.findItem(id)?.isChecked = true
            }
        }
    }
}
