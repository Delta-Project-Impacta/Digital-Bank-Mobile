package com.domleondev.deltabank.presentation.activities

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.domleondev.deltabank.R
import com.domleondev.deltabank.presentation.fragments.FragmentChat
import com.domleondev.deltabank.presentation.fragments.FragmentExtract
import com.domleondev.deltabank.presentation.fragments.FragmentHome
import com.domleondev.deltabank.presentation.fragments.FragmentProfile
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Ativa Edge-to-Edge
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // 2. Configura a transparência da Janela (Sua lógica estava certa)
        val window = window
        when {
            // Android 10 e anteriores (API < 30)
            Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

                @Suppress("DEPRECATION")
                window.statusBarColor = Color.TRANSPARENT
                @Suppress("DEPRECATION")
                window.navigationBarColor = Color.TRANSPARENT
            }

            // Android 11+ (API >= 30)
            else -> {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                val controller = WindowInsetsControllerCompat(window, window.decorView)

                // Configura ícones da barra de status (true = ícones escuros)
                // Se sua Home tem fundo escuro/laranja no topo, mude para false aqui ou no Fragment
                controller.isAppearanceLightStatusBars = true

                @Suppress("DEPRECATION")
                window.statusBarColor = Color.TRANSPARENT
                @Suppress("DEPRECATION")
                window.navigationBarColor = Color.TRANSPARENT
            }
        }

        // 3. Configuração de Navegação dos Fragments
        val navigation = findViewById<BottomNavigationView>(R.id.bottom_home_navigation)

        navigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_nav_Navigation_Home -> {
                    replaceFragment(FragmentHome.newInstance())
                    true
                }

                R.id.bottom_nav_Navigation_Chat -> {
                    replaceFragment(FragmentChat())
                    true
                }

                R.id.bottom_nav_Navigation_Extract -> {
                    replaceFragment(FragmentExtract())
                    true
                }

                R.id.bottom_nav_Navigation_Profile -> {
                    replaceFragment(FragmentProfile())
                    true
                }

                else -> false
            }
        }

        if (savedInstanceState == null) {
            navigation.selectedItemId = R.id.bottom_nav_Navigation_Home
        }

        // 4. A CORREÇÃO DO PADDING (O Grande Segredo)
        // Pegamos a referência do container da barra de navegação
        // (Certifique-se que esse ID existe no seu XML novo)
        // 4. A CORREÇÃO DO PADDING DUPLO

        val navContainer = findViewById<View>(R.id.bottom_nav_container)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->

            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // 1. Tela principal: Zera padding vertical para ocupar tudo

            v.setPadding(bars.left, 0, bars.right, 0)

            // 2. Container da Navbar: Aplica o padding manualmente (Nós controlamos isso)

            navContainer?.setPadding(0, 0, 0, bars.bottom)

            // 3. O TRUQUE: Cria um novo conjunto de insets com o bottom ZERADO

            // Isso impede que o BottomNavigationView (filho) receba o aviso da barra

            // e adicione mais espaço por conta própria.

            val newInsets = WindowInsetsCompat.Builder(insets)

                .setInsets(

                    WindowInsetsCompat.Type.systemBars(),

                    androidx.core.graphics.Insets.of(bars.left, bars.top, bars.right, 0)

                )

                .build()

            newInsets // Retorna o inset "mentiroso" para os filhos

        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, fragment, fragment.javaClass.simpleName)
            .commit()
    }
}