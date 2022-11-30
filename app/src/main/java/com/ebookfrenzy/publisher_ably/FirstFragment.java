package com.ebookfrenzy.publisher_ably;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.ably.tracking.Accuracy;
import com.ably.tracking.BuilderConfigurationIncompleteException;
import com.ably.tracking.ConnectionException;
import com.ably.tracking.Resolution;
import com.ably.tracking.connection.Authentication;
import com.ably.tracking.connection.ConnectionConfiguration;
import com.ably.tracking.publisher.DefaultResolutionPolicyFactory;
import com.ably.tracking.publisher.MapConfiguration;
import com.ably.tracking.publisher.Publisher;
import com.ably.tracking.publisher.java.PublisherFacade;
import com.ebookfrenzy.publisher_ably.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(view1 -> {
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            createAndStartPublisher();

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;

    public void createAndStartPublisher() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        Resolution defaultResolution = new Resolution(Accuracy.BALANCED, 1000L, 1.0);
        try {
            Publisher nativePublisher = Publisher.publishers()
                    .connection(new ConnectionConfiguration(Authentication.basic("CLIENT_ID", ""), null))
                    .map(new MapConfiguration(""))
                    .androidContext(getContext())
                    .resolutionPolicy(new DefaultResolutionPolicyFactory(defaultResolution, getContext()))
                    .backgroundTrackingNotificationProvider(
                            () -> new NotificationCompat.Builder(getContext(), "TRACKING_ID")
                                    .setContentTitle("Title")
                                    .setContentText("Text")
                                    .setSmallIcon(com.ably.tracking.publisher.java.R.drawable.aat_logo)
                                    .build(),
                            123
                    )
                    .start();
            PublisherFacade publisher = PublisherFacade.wrap(nativePublisher);
        } catch (BuilderConfigurationIncompleteException e) {
            e.printStackTrace();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

    }

}