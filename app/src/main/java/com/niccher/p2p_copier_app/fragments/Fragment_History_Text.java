package com.niccher.p2p_copier_app.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.niccher.p2p_copier_app.R;
import com.niccher.p2p_copier_app.adapters.Adapter_Rando;
import com.niccher.p2p_copier_app.interfaces.RetrofitInterface;
import com.niccher.p2p_copier_app.model.Mod_File_rando;
import com.niccher.p2p_copier_app.utils.Helpers;
import com.niccher.p2p_copier_app.utils.ServiceGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_History_Text extends Fragment {

    MaterialCardView card_last_sess;
    int click = 0;
    TextView card_last_label;
    View line;
    ImageView card_arrow_view;

    ArrayList<Mod_File_rando> my_got_file;
    Adapter_Rando list_got_files;
    RecyclerView recyclerView_got_files;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_history_text, container, false);

        card_last_sess = view.findViewById(R.id.card_last_session);
        card_last_label = view.findViewById(R.id.card_last_label);
        line = view.findViewById(R.id.line);
        card_arrow_view = view.findViewById(R.id.card_arrow_view);

        recyclerView_got_files = view.findViewById(R.id.card_text_items_RecyclerView);
        recyclerView_got_files.setHasFixedSize(true);
        recyclerView_got_files.setLayoutManager(new LinearLayoutManager(getActivity()));

        my_got_file = new ArrayList<>();

        card_last_sess.setOnClickListener(v -> {
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
                                recyclerView_got_files.setVisibility(View.GONE);
                                super.onAnimationEnd(animation);
                            }
                        });
                card_arrow_view.setImageResource(R.drawable.ic_arrow_drop_down);
            }
            click++;
        });

        loadTextHistory();

        return view;
    }

    private void loadTextHistory() {
        if (getActivity() == null) return;

        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class, getActivity());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("var_dev_uuid", Helpers.get_prefs_dev("dev_uuid", getActivity()));
        parameters.put("var_auth_code_id", Helpers.get_prefs_sess("auth_auth_code_id", getActivity()));

        Call<ResponseBody> call = retrofitInterface.getTextsUploadedbySessDevid(parameters);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String rawJson = response.body().string();
                        Gson gson = new Gson();
                        JsonObject obj = gson.fromJson(rawJson, JsonObject.class);
                        if (obj.has("texts") && obj.get("texts").isJsonArray()) {
                            JsonArray textsArray = obj.getAsJsonArray("texts");
                            my_got_file.clear();
                            for (JsonElement elem : textsArray) {
                                JsonObject textObj = elem.getAsJsonObject();
                                String title = textObj.has("text_title") && !textObj.get("text_title").isJsonNull()
                                        ? textObj.get("text_title").getAsString() : "Text Entry";
                                String content = textObj.has("text_content") && !textObj.get("text_content").isJsonNull()
                                        ? textObj.get("text_content").getAsString() : "";
                                String date = textObj.has("text_created_at") && !textObj.get("text_created_at").isJsonNull()
                                        ? textObj.get("text_created_at").getAsString() : "";
                                String source = textObj.has("text_source") && !textObj.get("text_source").isJsonNull()
                                        ? textObj.get("text_source").getAsString() : "Android";

                                my_got_file.add(new Mod_File_rando(title, date, source, content));
                            }
                            showAddedFile(my_got_file);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error loading text history: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showAddedFile(ArrayList<Mod_File_rando> my_got_file_passed) {
        list_got_files = new Adapter_Rando(my_got_file_passed, getActivity());
        recyclerView_got_files.setAdapter(list_got_files);
        list_got_files.notifyDataSetChanged();
    }
}
