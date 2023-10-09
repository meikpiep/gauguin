package org.piepmeyer.gauguin.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentKeyPadBinding
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GridCreationListener
import org.piepmeyer.gauguin.options.CurrentGameOptionsVariant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.ceil

class KeyPadFragment : Fragment(R.layout.fragment_key_pad), GridCreationListener, KoinComponent {
    private val game: Game by inject()

    private lateinit var binding: FragmentKeyPadBinding
    private val numbers = mutableListOf<MaterialButton>()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKeyPadBinding.inflate(inflater, parent, false)
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

        game.addGridCreationListener(this)
    }

    private fun addButtonListeners(numberButton: MaterialButton) {
        numberButton.setOnClickListener {
            val d = numberButton.text.toString().toInt()
            game.enterPossibleNumber(d)
        }
        numberButton.setOnLongClickListener {
            val d = numberButton.text.toString().toInt()
            game.enterNumber(d)
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
        val digits = digitSetting.numbers.iterator()

        if (digitSetting.containsZero()) {
            digits.next()
        }

        val visibleRows = ceil(game.grid.variant.possibleDigits.size / 3.0).toInt()
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
            it.isEnabled = game.grid.variant.possibleDigits.contains(it.text.toString().toInt())
        }
        binding.controls.visibility = View.VISIBLE
    }
}