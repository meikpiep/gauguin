package com.holokenmod.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.holokenmod.R;
import com.holokenmod.creation.GridDifficultyCalculator;
import com.holokenmod.databinding.GameTopFragmentBinding;
import com.holokenmod.game.Game;
import com.holokenmod.options.GameDifficulty;
import com.holokenmod.ui.GridCreationListener;

public class GameTopFragment extends Fragment implements GridCreationListener {

    private Game game;
    
    private GameTopFragmentBinding binding;
    
    private boolean showtimer;
    
    public GameTopFragment() {
        super(R.layout.game_top_fragment);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        binding = GameTopFragmentBinding.inflate(inflater, parent, false);
        
        return binding.getRoot();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        if (game != null) {
            freshGridWasCreated();
        }
    }
    
    public void freshGridWasCreated() {
        if (!isAdded()) {
            return;
        }
        
        requireActivity().runOnUiThread( () -> {
            GridDifficultyCalculator difficultyCalculator = new GridDifficultyCalculator(this.game.getGrid());
    
            binding.difficulty.setText(difficultyCalculator.getInfo());
            
            setStarsByDifficulty(difficultyCalculator);
            
            if(this.showtimer) {
                binding.playtime.setVisibility(View.VISIBLE);
            } else {
                binding.playtime.setVisibility(View.INVISIBLE);
            }
        });
    }
    
    public void setGame(Game game) {
        this.game = game;
        
        freshGridWasCreated();
    }
    
    private void setStarsByDifficulty(GridDifficultyCalculator difficultyCalculator) {
        setStarByDifficulty(binding.ratingStarOne,
                difficultyCalculator.getDifficulty(),
                GameDifficulty.EASY);
        setStarByDifficulty(binding.ratingStarTwo,
                difficultyCalculator.getDifficulty(),
                GameDifficulty.MEDIUM);
        setStarByDifficulty(binding.ratingStarThree,
                difficultyCalculator.getDifficulty(),
                GameDifficulty.HARD);
        setStarByDifficulty(binding.ratingStarFour,
                difficultyCalculator.getDifficulty(),
                GameDifficulty.EXTREME);
    }
    
    private void setStarByDifficulty(ImageView view, GameDifficulty difficulty, GameDifficulty minimumDifficulty) {
        if (difficulty.compareTo(minimumDifficulty) >= 0) {
            view.setImageResource(R.drawable.filled_star_20);
        } else {
            view.setImageResource(R.drawable.outline_star_20);
        }
    }
    
    public void setGameTime(String timeDescription) {
        binding.playtime.setText(timeDescription);
    }
    
    public void setTimerVisible(boolean showtimer) {
        this.showtimer = showtimer;
    }
}