package com.holokenmod.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.holokenmod.R
import com.holokenmod.databinding.KeyPadFragmentBinding
import com.holokenmod.game.Game
import com.holokenmod.options.ApplicationPreferences
import com.holokenmod.options.CurrentGameOptionsVariant
import com.holokenmod.ui.GridCreationListener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.ceil

class KeyPadFragment : Fragment(R.layout.key_pad_fragment), GridCreationListener, KoinComponent {
    private val game: Game by inject()
    private val applicationPreferences: ApplicationPreferences by inject()

    private lateinit var binding: KeyPadFragmentBinding
    private val numbers = mutableListOf<MaterialButton>()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = KeyPadFragmentBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        numbers += binding.button1
        numbers += binding.button2
        numbers += binding.button3
        numbers += binding.button4
        numbers += binding.button5
        numbers += binding.button6
        numbers += binding.button7
        numbers += binding.button8
        numbers += binding.button9
        numbers += binding.button10
        numbers += binding.button11
        numbers += binding.button12
                
        numbers.forEach {
            addButtonListeners(it)
        }

        setButtonLabels()
        setButtonVisibility()
    }

    private fun addButtonListeners(numberButton: MaterialButton) {
        numberButton.setOnClickListener {
            val d = numberButton.text.toString().toInt()
            game.enterPossibleNumber(d)
        }
        numberButton.setOnLongClickListener {
            val d = numberButton.text.toString().toInt()
            game.enterNumber(d, applicationPreferences.removePencils())
            true
        }
    }

    override fun freshGridWasCreated() {
        if (!isAdded) {
            return
        }
        requireActivity().runOnUiThread {
            setButtonLabels()
            setButtonVisibility()
        }
    }

    private fun setButtonLabels() {
        val digitSetting = CurrentGameOptionsVariant.instance.digitSetting
        val digits = digitSetting.allNumbers.iterator()

        if (digitSetting.containsZero()) {
            digits.next()
        }

        val visibleRows = ceil(game.grid.possibleDigits.size / 3.0).toInt()
        val lastVisibleNumber = visibleRows * 3 - 1

        var i = 0

        numbers.forEach {
            val digit: Int = if (i == lastVisibleNumber && digitSetting.containsZero()) {
                0
            } else {
                digits.next()
            }
            it.text = digit.toString()
            it.visibility = if (i <= lastVisibleNumber) View.VISIBLE else View.GONE
            i++
        }
    }

    private fun setButtonVisibility() {
        numbers.forEach {
            it.isEnabled = game.grid.possibleDigits.contains(it.text.toString().toInt())
        }
        binding.controls.visibility = View.VISIBLE
    }

    fun setGame(game: Game) {
        freshGridWasCreated()
    }
}