    package com.example.helloworldproject.ui.fragments;

    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;

    import androidx.fragment.app.Fragment;
    import androidx.lifecycle.ViewModelProvider;

    import com.example.helloworldproject.R;
    import com.example.helloworldproject.model.Event;
    import com.example.helloworldproject.ui.event.LotteryTextBuilder;
    import com.google.android.material.appbar.MaterialToolbar;

    public class EventDetailFragment extends Fragment {

        protected EventDetailViewModel viewModel;

        private Button btnPrimary, btnSecondaryAccept, btnLotteryRules;
        private Button btnSecondaryDecline = null;
        private View progressGroup;
        private TextView tvDesc, tvVenue, tvWaitlistCount;

        private Event currentEvent;


        private int currentWaitlistCount;

        @Override
        public void onCreate(Bundle savedInstances) {
            super.onCreate(savedInstances);
            viewModel = new ViewModelProvider(requireActivity()).get(EventDetailViewModel.class);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.event_detail, container, false);

            btnPrimary = view.findViewById(R.id.button); // Assuming ID 'button' in single layout
            btnSecondaryAccept = view.findViewById(R.id.button1);
            btnSecondaryDecline = view.findViewById(R.id.button2);
            progressGroup = view.findViewById(R.id.progressGroup);
            btnLotteryRules = view.findViewById(R.id.btn_lottery_rules);
            tvDesc = view.findViewById(R.id.descriptionValue);
            tvWaitlistCount = view.findViewById(R.id.waitingValue);
            tvVenue = view.findViewById(R.id.venue_value);

            MaterialToolbar appBar = view.findViewById(R.id.topAppBar);


            // 2. SET LISTENERS ONCE
            btnPrimary.setOnClickListener(v -> viewModel.onButtonClicked());
            btnSecondaryAccept.setOnClickListener(v -> viewModel.onButtonClicked());
            btnSecondaryDecline.setOnClickListener(v -> viewModel.onButton2Clicked());

            // Show lottery rules (US 01.05.05).
            btnLotteryRules.setOnClickListener(v -> {
                if (currentEvent == null) return;
                String msg = LotteryTextBuilder.build(currentEvent, currentWaitlistCount);
                LotteryTextBuilder.showDialog(requireActivity(), msg);
            });

            viewModel.getEvent().observe(getViewLifecycleOwner(), e -> {
                currentEvent = e;
                if (e.getDescription() == null) {
                    tvDesc.setText("No description available.");
                } else {
                    tvDesc.setText(e.getDescription());
                }
                if (appBar != null) {
                    String title = currentEvent.getTitle();
                    if (title != null) {
                        appBar.setTitle(title);
                    } else {
                        appBar.setTitle("Untitled");
                    }
                }
                tvVenue.setText(e.getVenue());

            });
            viewModel.getEntrantState().observe(getViewLifecycleOwner(), this::setPage );
            viewModel.getCurrentWaitlistCount().observe(getViewLifecycleOwner(), count -> {
                currentWaitlistCount = count;
                tvWaitlistCount.setText(String.valueOf(currentWaitlistCount) + "/50");
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
                btnPrimary.setVisibility(View.GONE);
                progressGroup.setVisibility(View.VISIBLE);
                btnSecondaryAccept.setText(viewModel.getButtonText().getValue());
            } else {
                btnPrimary.setVisibility(View.VISIBLE);
                progressGroup.setVisibility(View.GONE);
                btnPrimary.setText(viewModel.getButtonText().getValue());
            }
        }
    }
