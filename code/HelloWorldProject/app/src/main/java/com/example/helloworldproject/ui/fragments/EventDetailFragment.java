    package com.example.helloworldproject.ui.fragments;

    import android.content.Context;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;

    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;
    import androidx.lifecycle.ViewModelProvider;

    import com.example.helloworldproject.R;
    import com.example.helloworldproject.model.Event;
    import com.example.helloworldproject.ui.event.EventDetailActivity;
    import com.example.helloworldproject.ui.event.LotteryTextBuilder;

    public class EventDetailFragment extends Fragment {

        protected EventDetailViewModel viewModel;

        private Button singleButton;

        private Button doubleButton1;
        private Button doubleButton2 = null;

        private Event currentEvent;
        private Button btnLotteryRules;

        private View progressGroup;

        private int currentWaitlistCount;

        @Override
        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            viewModel = new ViewModelProvider(requireActivity()).get(EventDetailViewModel.class);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.event_detail, container, false);

            singleButton = view.findViewById(R.id.button); // Assuming ID 'button' in single layout
            doubleButton1 = view.findViewById(R.id.button1);
            doubleButton2 = view.findViewById(R.id.button2);
            progressGroup = view.findViewById(R.id.progressGroup);
            btnLotteryRules = view.findViewById(R.id.btn_lottery_rules);

            // 2. SET LISTENERS ONCE
            singleButton.setOnClickListener(v -> viewModel.onButtonClicked());
            doubleButton1.setOnClickListener(v -> viewModel.onButtonClicked());
            doubleButton2.setOnClickListener(v -> viewModel.onButton2Clicked());

            // Show lottery rules (US 01.05.05).
            btnLotteryRules.setOnClickListener(v -> {
                if (currentEvent == null) return;
                String msg = LotteryTextBuilder.build(currentEvent, currentWaitlistCount);
                LotteryTextBuilder.showDialog(requireActivity(), msg);
            });

            viewModel.getEvent().observe(getViewLifecycleOwner(), e -> {
                currentEvent = e;
            });
            viewModel.getEntrantState().observe(getViewLifecycleOwner(), this::setPage );
            viewModel.getCurrentWaitlistCount().observe(getViewLifecycleOwner(), count -> {
                currentWaitlistCount = count;
            });

            EntrantState state = viewModel.getEntrantState().getValue();
            this.setPage(state);

            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }

        private void setPage(EntrantState status) {
            if (status == EntrantState.INVITED) {
                singleButton.setVisibility(View.GONE);
                progressGroup.setVisibility(View.VISIBLE);
                doubleButton1.setText(viewModel.getButtonText().getValue());
            } else {
                singleButton.setVisibility(View.VISIBLE);
                progressGroup.setVisibility(View.GONE);
                singleButton.setText(viewModel.getButtonText().getValue());
            }
        }
    }
