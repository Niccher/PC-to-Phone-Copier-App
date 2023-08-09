package com.niccher.pctophonecopier.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.adapters.Adapter_Rando;
import com.niccher.pctophonecopier.model.Mod_File_rando;

import java.util.ArrayList;

public class Fragment_History_Text extends Fragment {

    MaterialCardView card_last_sess;
    int click = 0;
    TextView card_last_label;
    View line;
    ImageView card_arrow_view;

    ArrayList<String> sel_files;
    ArrayList<Mod_File_rando> my_got_file;

    Adapter_Rando list_got_files;
    RecyclerView recyclerView_got_files;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_history_text,container,false);

        card_last_sess = view.findViewById(R.id.card_last_session);
        card_last_label = view.findViewById(R.id.card_last_label);
        line = view.findViewById(R.id.line);
        card_arrow_view = view.findViewById(R.id.card_arrow_view);

        recyclerView_got_files = view.findViewById(R.id.card_text_items_RecyclerView);
        recyclerView_got_files.setHasFixedSize(true);
        recyclerView_got_files.setLayoutManager(new LinearLayoutManager(getActivity()));

        my_got_file = new ArrayList<Mod_File_rando>(1);
        sel_files = new ArrayList<String>(1);

        for (int i = 0; i < 5; i++) {
            sel_files.add("44645");
            my_got_file.add(new Mod_File_rando("7899", "9999","1111", "uri_selected_file"));
            showAddedFile(my_got_file);
        }

        card_last_sess.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(card_last_sess);
                if (click % 2 == 0) {
                    card_last_label.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    card_last_label.setVisibility(View.VISIBLE);
                                    line.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getContext(), "Number Shown", Toast.LENGTH_SHORT).show();
                                    recyclerView_got_files.setVisibility(View.VISIBLE);
                                    super.onAnimationEnd(animation);
                                }
                            });
                    card_arrow_view.setImageResource(R.drawable.ic_arrow_drop_up);
                } else {
                    card_last_label.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    card_last_label.setVisibility(View.VISIBLE);
                                    line.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getContext(), "Number Hidden", Toast.LENGTH_SHORT).show();
                                    recyclerView_got_files.setVisibility(View.GONE);
                                    super.onAnimationEnd(animation);
                                }
                            });
                    card_arrow_view.setImageResource(R.drawable.ic_arrow_drop_down);
                }
                click++;
            }
        });

        return view;
    }

    public void showAddedFile(ArrayList<Mod_File_rando> my_got_file_passed) {
        list_got_files = new Adapter_Rando(my_got_file_passed, getActivity());
        recyclerView_got_files.setAdapter(list_got_files);
        list_got_files.notifyDataSetChanged();
    }
}
