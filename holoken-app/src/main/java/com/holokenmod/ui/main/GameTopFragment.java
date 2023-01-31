package com.holokenmod.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.holokenmod.R;
import com.holokenmod.creation.GridDifficultyCalculator;
import com.holokenmod.game.Game;
import com.holokenmod.options.GameDifficulty;
import com.holokenmod.ui.GridCreationListener;

public class GameTopFragment extends Fragment implements GridCreationListener {

    private Game game;
    
    private ImageView ratingStarOne;
    
    private ImageView ratingStarTwo;
    
    private ImageView ratingStarThree;
    
    private ImageView ratingStarFour;
    
    private TextView difficultyText;
    
    private TextView timeView;
    
    private boolean showtimer;
    
    public GameTopFragment() {
        super(R.layout.game_top_fragment);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_top_fragment, parent, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        
        this.ratingStarOne = view.findViewById(R.id.ratingStarOne);
        this.ratingStarTwo = view.findViewById(R.id.ratingStarTwo);
        this.ratingStarThree = view.findViewById(R.id.ratingStarThree);
        this.ratingStarFour = view.findViewById(R.id.ratingStarFour);
        
        this.difficultyText = view.findViewById(R.id.difficulty);
    
        this.timeView = view.findViewById(R.id.playtime);
        
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
    
            difficultyText.setText(difficultyCalculator.getInfo());
            
            setStarsByDifficulty(difficultyCalculator);
            
            if(this.showtimer) {
                this.timeView.setVisibility(View.VISIBLE);
            } else {
                this.timeView.setVisibility(View.INVISIBLE);
            }
        });
    }
    
    public void setGame(Game game) {
        this.game = game;
        
        freshGridWasCreated();
    }
    
    private void setStarsByDifficulty(GridDifficultyCalculator difficultyCalculator) {
        setStarByDifficulty(this.ratingStarOne,
                difficultyCalculator.getDifficulty(),
                GameDifficulty.EASY);
        setStarByDifficulty(this.ratingStarTwo,
                difficultyCalculator.getDifficulty(),
                GameDifficulty.MEDIUM);
        setStarByDifficulty(this.ratingStarThree,
                difficultyCalculator.getDifficulty(),
                GameDifficulty.HARD);
        setStarByDifficulty(this.ratingStarFour,
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
        this.timeView.setText(timeDescription);
    }
    
    public void setTimerVisible(boolean showtimer) {
        this.showtimer = showtimer;
    }
}