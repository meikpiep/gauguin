package com.holokenmod.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.holokenmod.Game;
import com.holokenmod.R;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.options.GameVariant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeyPadFragment extends Fragment implements GridCreationListener {
    private final List<Button> numbers = new ArrayList<>();
    private final List<Button> allNumbers = new ArrayList<>();
    private Button numberExtra;
    private TableLayout controlKeypad;
    private Game game;
    
    public KeyPadFragment() {
        super(R.layout.key_pad_fragment);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.key_pad_fragment, parent, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        numbers.add(view.findViewById(R.id.button1));
        numbers.add(view.findViewById(R.id.button2));
        numbers.add(view.findViewById(R.id.button3));
        numbers.add(view.findViewById(R.id.button4));
        numbers.add(view.findViewById(R.id.button5));
        numbers.add(view.findViewById(R.id.button6));
        numbers.add(view.findViewById(R.id.button7));
        numbers.add(view.findViewById(R.id.button8));
        numbers.add(view.findViewById(R.id.button9));
        numbers.add(view.findViewById(R.id.button10));
        numberExtra = view.findViewById(R.id.buttonExtra);
    
        allNumbers.addAll(numbers);
        allNumbers.add(numberExtra);
    
        this.controlKeypad = view.findViewById(R.id.controls);
    
        //this.controlKeypad.setVisibility(View.INVISIBLE);
    
        for (final Button numberButton : numbers) {
            addButtonListeners(numberButton);
        }
        addButtonListeners(numberExtra);

        if (game != null) {
            setButtonLabels();
            setButtonVisibility();
        }
    }
    
    private void addButtonListeners(Button numberButton) {
        numberButton.setOnClickListener(v -> {
            final int d = Integer.parseInt(((Button) v).getText().toString());
            game.enterPossibleNumber(d);
        });
        numberButton.setOnLongClickListener(v -> {
            final int d = Integer.parseInt(((Button) v).getText().toString());
            game.enterNumber(d);
            
            return true;
        });
    }
    
    
    public void freshGridWasCreated() {
        if (!isAdded()) {
            return;
        }
    
        requireActivity().runOnUiThread( () -> {
            setButtonLabels();
            setButtonVisibility();
        });
    }
    
    private void setButtonLabels() {
        DigitSetting digitSetting = GameVariant.getInstance().getDigitSetting();
        
        /*if (digitSetting == DigitSetting.FIRST_DIGIT_ZERO) {
            numberExtra.setText("0");
        } else {
            numberExtra.setText("11");
        }*/
        
        Iterator<Integer> digits = digitSetting.getAllNumbers().iterator();
        
        for (final Button numberButton : numbers) {
            int digit = digits.next();
            
            numberButton.setText(Integer.toString(digit));
            numberButton.setVisibility(View.VISIBLE);
        }
    }
    
    private void setButtonVisibility() {
        DigitSetting digitSetting = GameVariant.getInstance().getDigitSetting();
        
        for (int i = 0; i < numbers.size(); i++) {
            numbers.get(i).setEnabled(i < digitSetting.getMaximumDigit(game.getGrid().getGridSize()));
        }
        
        boolean useExtraNumber = digitSetting == DigitSetting.FIRST_DIGIT_ZERO
                || game.getGrid().getGridSize().getAmountOfNumbers() >= 11;
        
        numberExtra.setEnabled(useExtraNumber);
        
        this.controlKeypad.setVisibility(View.VISIBLE);
    }
    
    public void setGame(Game game) {
        this.game = game;
    
        freshGridWasCreated();
    }
}