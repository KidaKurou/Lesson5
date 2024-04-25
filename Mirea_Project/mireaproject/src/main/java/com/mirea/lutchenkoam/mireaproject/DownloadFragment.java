package com.mirea.lutchenkoam.mireaproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

public class DownloadFragment extends Fragment {
    private ProgressBar progressBar;

    public DownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        Button buttonStartDownload = view.findViewById(R.id.button_start_download);

        buttonStartDownload.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            OneTimeWorkRequest downloadRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class).build();
            WorkManager.getInstance(requireContext()).enqueue(downloadRequest);

            WorkManager.getInstance(requireContext())
                    .getWorkInfoByIdLiveData(downloadRequest.getId())
                    .observe(getViewLifecycleOwner(), workInfo -> {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        });

        return view;
    }
}