package com.holokenmod.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.holokenmod.R
import com.holokenmod.databinding.KeyPadFragmentBinding
import com.holokenmod.game.Game
import com.holokenmod.options.ApplicationPreferences
import com.holokenmod.options.CurrentGameOptionsVariant
import com.holokenmod.ui.GridCreationListener
import kotlin.math.ceil

class KeyPadFragment : Fragment(R.layout.key_pad_fragment), GridCreationListener {
    private val numbers = mutableListOf<MaterialButton>()
    private var controlKeypad: TableLayout? = null
    private var game: Game? = null
    private var binding: KeyPadFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = KeyPadFragmentBinding.inflate(inflater, parent, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        numbers += binding!!.button1
        numbers += binding!!.button2
        numbers += binding!!.button3
        numbers += binding!!.button4
        numbers += binding!!.button5
        numbers += binding!!.button6
        numbers += binding!!.button7
        numbers += binding!!.button8
        numbers += binding!!.button9
        numbers += binding!!.button10
        numbers += binding!!.button11
        numbers += binding!!.button12
                
        controlKeypad = binding!!.controls

        numbers.forEach {
            addButtonListeners(it)
        }

        if (game != null) {
            setButtonLabels()
            setButtonVisibility()
        }
    }

    private fun addButtonListeners(numberButton: MaterialButton) {
        numberButton.setOnClickListener {
            val d = numberButton.text.toString().toInt()
            game!!.enterPossibleNumber(d)
        }
        numberButton.setOnLongClickListener {
            val d = numberButton.text.toString().toInt()
            game!!.enterNumber(d, ApplicationPreferences.instance.removePencils())
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
        val visibleRows = ceil(game!!.grid.possibleDigits.size / 3.0).toInt()
        val lastVisibleNumber = visibleRows * 3 - 1
        var i = 0
        for (numberButton in numbers) {
            val digit: Int = if (i == lastVisibleNumber && digitSetting.containsZero()) {
                0
            } else {
                digits.next()
            }
            numberButton.text = digit.toString()
            numberButton.visibility = if (i <= lastVisibleNumber) View.VISIBLE else View.GONE
            i++
        }
    }

    private fun setButtonVisibility() {
        for (number in numbers) {
            number.isEnabled = game!!.grid.possibleDigits.contains(number.text.toString().toInt())
        }
        controlKeypad!!.visibility = View.VISIBLE
    }

    fun setGame(game: Game) {
        this.game = game
        freshGridWasCreated()
    }
}