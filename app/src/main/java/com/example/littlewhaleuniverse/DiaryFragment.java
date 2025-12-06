package com.example.littlewhaleuniverse;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;

public class DiaryFragment extends Fragment {
    private DiaryViewModel viewModel;
    private DiaryListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_diaries);
        adapter = new DiaryListAdapter(new DiaryListAdapter.DiaryDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(DiaryViewModel.class);
        viewModel.getAllDiaries().observe(getViewLifecycleOwner(), diaries -> adapter.submitList(diaries));

        FloatingActionButton fab = view.findViewById(R.id.fab_add_diary);
        fab.setOnClickListener(v -> startActivity(new Intent(getContext(), EditDiaryActivity.class)));

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView rv, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder target) { return false; }
            @Override
            public void onSwiped(RecyclerView.ViewHolder vh, int direction) {
                int position = vh.getAdapterPosition();
                List<DiaryEntry> list = adapter.getCurrentList();
                if (position >= 0 && position < list.size()) {
                    DiaryEntry diary = list.get(position);
                    viewModel.delete(diary);
                    Snackbar.make(recyclerView, "Deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> viewModel.insert(diary))
                            .show();
                }
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }
}
