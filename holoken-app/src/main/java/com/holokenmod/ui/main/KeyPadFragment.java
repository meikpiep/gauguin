package com.holokenmod.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.holokenmod.game.Game;
import com.holokenmod.R;
import com.holokenmod.options.ApplicationPreferences;
import com.holokenmod.options.CurrentGameOptionsVariant;
import com.holokenmod.options.DigitSetting;
import com.holokenmod.ui.GridCreationListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KeyPadFragment extends Fragment implements GridCreationListener {
    private final List<MaterialButton> numbers = new ArrayList<>();
    private TableLayout controlKeypad;
    private Game game;
    
    public KeyPadFragment() {
        super(R.layout.key_pad_fragment);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
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
        numbers.add(view.findViewById(R.id.button11));
        numbers.add(view.findViewById(R.id.button12));
        
        this.controlKeypad = view.findViewById(R.id.controls);
        
        for (final MaterialButton numberButton : numbers) {
            addButtonListeners(numberButton);
        }
        
        if (game != null) {
            setButtonLabels();
            setButtonVisibility();
        }
    }
    
    private void addButtonListeners(MaterialButton numberButton) {
        numberButton.setOnClickListener(v -> {
            final int d = Integer.parseInt(((MaterialButton) v).getText().toString());
            game.enterPossibleNumber(d);
        });
        numberButton.setOnLongClickListener(v -> {
            final int d = Integer.parseInt(((MaterialButton) v).getText().toString());
            game.enterNumber(d, ApplicationPreferences.getInstance().removePencils());
            
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
        DigitSetting digitSetting = CurrentGameOptionsVariant.getInstance().getDigitSetting();
        
        Iterator<Integer> digits = digitSetting.getAllNumbers().iterator();
        
        if (digitSetting.containsZero()) {
            digits.next();
        }
        
        int visibleRows = ((int) Math.ceil(game.getGrid().getPossibleDigits().size() / 3.0));
        int lastVisibleNumber = visibleRows * 3 - 1;
        int i = 0;
        
        for (final Button numberButton : numbers) {
            int digit;
            
            if (i == lastVisibleNumber && digitSetting.containsZero()) {
                digit = 0;
            } else {
            digit = digits.next();
            }
            
            numberButton.setText(Integer.toString(digit));
            numberButton.setVisibility(i <= lastVisibleNumber ? View.VISIBLE : View.GONE);
            
            i++;
        }
    }
    
    private void setButtonVisibility() {
        for (MaterialButton number : numbers) {
            number.setEnabled(game.getGrid().getPossibleDigits().contains(Integer.parseInt(number.getText().toString())));
        }
        
        this.controlKeypad.setVisibility(View.VISIBLE);
    }
    
    public void setGame(Game game) {
        this.game = game;
        
        freshGridWasCreated();
    }
}