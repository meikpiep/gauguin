package org.piepmeyer.gauguin.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.piepmeyer.gauguin.R
import org.piepmeyer.gauguin.databinding.FragmentKeyPadBinding
import org.piepmeyer.gauguin.game.Game
import org.piepmeyer.gauguin.game.GameModeListener
import org.piepmeyer.gauguin.game.GridCreationListener
import kotlin.math.ceil
import kotlin.math.roundToInt

class KeyPadFragment :
    Fragment(R.layout.fragment_key_pad),
    GridCreationListener,
    KoinComponent,
    GameModeListener {
    private val game: Game by inject()

    private lateinit var binding: FragmentKeyPadBinding
    private val numbers = mutableListOf<MaterialButton>()

    private val numberButtonToDigit = mutableMapOf<MaterialButton, Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentKeyPadBinding.inflate(inflater, parent, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
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

        setButtonStates()

        game.addGridCreationListener(this)
        game.addGameModeListener(this)
    }

    private fun addButtonListeners(numberButton: MaterialButton) {
        numberButton.setOnClickListener {
            game.enterPossibleNumber(numberButtonToDigit[numberButton]!!)
        }
        numberButton.setOnLongClickListener {
            game.enterNumber(numberButtonToDigit[numberButton]!!)
            true
        }
    }

    override fun freshGridWasCreated() {
        if (!isAdded) {
            return
        }
        requireActivity().runOnUiThread {
            setButtonStates()
        }
    }

    private fun setButtonStates() {
        val visibleRows = ceil(game.grid.variant.possibleDigits.size / 3.0).toInt()
        val lastVisibleNumber = visibleRows * 3 - 1

        val digitSetting = game.grid.variant.options.digitSetting
        val digits = digitSetting.numbers.toMutableList()

        if (digits[0] == 0) {
            digits.remove(0)
            digits.add(lastVisibleNumber, 0)
        }

        val digitsIterator = digits.iterator()

        var i = 0

        numberButtonToDigit.clear()

        numbers.forEach {
            val digit = digitsIterator.next()

            numberButtonToDigit[it] = digit
            it.text = game.grid.variant.options.numeralSystem.displayableString(digit)

            if (it.text.length > 4) {
                val cutTextIndex = (it.text.length / 2 + 0.4).roundToInt()

                it.text = "${it.text.subSequence(0, cutTextIndex)}\n${it.text.subSequence(cutTextIndex, it.text.length)}"
            }

            it.visibility =
                when {
                    (i <= lastVisibleNumber) -> View.VISIBLE
                    else -> View.GONE
                }
            it.isEnabled = game.grid.variant.possibleDigits.contains(digit) && !game.isInFastFinishingMode()
            i++
        }
    }

    override fun changedGameMode() {
        setButtonStates()
    }
}
