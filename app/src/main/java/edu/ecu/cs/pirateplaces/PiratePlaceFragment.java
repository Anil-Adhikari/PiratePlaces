package edu.ecu.cs.pirateplaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import edu.ecu.cs.pirateplaces.databinding.FragmentPiratePlaceBinding;
import edu.ecu.cs.pirateplaces.databinding.FragmentPiratePlaceImageBinding;

/**
 * Displays an individual Pirate Place so it can be edited
 *
 * @author Mark Hills (mhills@cs.ecu.edu)
 * @version 1.2
 */
public class PiratePlaceFragment extends Fragment
{
    /** The tag for the ID of the Pirate Place to be edited */
    private static final String ARG_PLACE_ID = "place_id";

    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO = 2;

    /** The registered callback used to communicate info back to the hosting activity */
    private Callbacks mCallbacks;

    /** The adapter for the recycler view, used to hold images of this pirate place */
    private ImageAdapter mAdapter;

    /** The data binding class used to communicate with the view */
    private FragmentPiratePlaceBinding mBinding;

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onPiratePlaceUpdated(PiratePlace piratePlace);
        void onPiratePlaceDeleted(PiratePlace piratePlace);
    }

    public static PiratePlaceFragment newInstance(UUID id)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLACE_ID, id);

        PiratePlaceFragment fragment = new PiratePlaceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    public static UUID getUpdatedId(Intent data)
    {
        return (UUID) data.getSerializableExtra(PiratePlaceFragment.ARG_PLACE_ID);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        PirateBase pirateBase = PirateBase.getPirateBase(getActivity());
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pirate_place, container, false);
        mBinding.setViewModel(new PiratePlaceViewModel(pirateBase, getContext()));

        UUID id = (UUID) getArguments().getSerializable(ARG_PLACE_ID);
        mBinding.getViewModel().setPiratePlace(pirateBase.getPiratePlace(id));

        mBinding.piratePlaceName.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                mBinding.getViewModel().setPlaceName(s.toString());
                updatePiratePlace();
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        mBinding.piratePlaceLastVisited.setKeyListener(null);

        mBinding.checkInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mBinding.getViewModel().setLastVisitedDate(new Date());
                updatePiratePlace();
            }
        });

        mBinding.editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mBinding.getViewModel().getPiratePlace().getLastVisited());
                dialog.setTargetFragment(PiratePlaceFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mBinding.editTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment
                        .newInstance(mBinding.getViewModel().getPiratePlace().getLastVisited());
                dialog.setTargetFragment(PiratePlaceFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mBinding.sendToFriend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent sendMessage =
                        new Intent(Intent.ACTION_SEND)
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_subject))
                        .putExtra(Intent.EXTRA_TEXT, getMessageText())
                        ;
                sendMessage = Intent.createChooser(sendMessage, getString(R.string.send_message_title));
                startActivity(sendMessage);
            }
        });

        mBinding.piratePlaceImagesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        updateUI();

        return mBinding.getRoot();
    }

    protected void updateUI() {
        PirateBase pirateBase = PirateBase.getPirateBase(getActivity());
        List<File> imageFiles = pirateBase.getPhotoFiles(mBinding.getViewModel().getPiratePlace());

        if (mAdapter == null) {
            mAdapter = new ImageAdapter(imageFiles);
            mBinding.piratePlaceImagesRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.updateImageFiles(imageFiles);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE && data != null) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mBinding.getViewModel().setLastVisitedDate(date);
            updatePiratePlace();
        } else if (requestCode == REQUEST_TIME && data != null) {
            Date date = (Date) data
                    .getSerializableExtra(TimePickerFragment.EXTRA_DATE);
            mBinding.getViewModel().setLastVisitedDate(date);
            updatePiratePlace();
        } else if (requestCode == REQUEST_PHOTO) {
            updateUI();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_pirate_place, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.delete_place:
                PirateBase.getPirateBase(getActivity()).deletePiratePlace(mBinding.getViewModel().getPiratePlace());
                mCallbacks.onPiratePlaceDeleted(mBinding.getViewModel().getPiratePlace());
                return true;
            case R.id.take_picture:
                PirateBase pirateBase = PirateBase.getPirateBase(getActivity());
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "edu.ecu.cs.pirateplaces.fileprovider",
                        pirateBase.getNewPhotoFile(mBinding.getViewModel().getPiratePlace()));

                final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getMessageText()
    {
        String dateFormat = "MMMM d, yyyy";
        String dateString = DateFormat.format(dateFormat, mBinding.getViewModel().getLastVisitedDate()).toString();

        String timeFormat = "K:mm a";
        String timeString = DateFormat.format(timeFormat, mBinding.getViewModel().getLastVisitedDate()).toString();

        String message = getString(R.string.message_text,
                mBinding.getViewModel().getPlaceName(),
                dateString,
                timeString);

        return message;
    }

    private void updatePiratePlace()
    {
        mCallbacks.onPiratePlaceUpdated(mBinding.getViewModel().getPiratePlace());
    }

    private class ImageHolder extends RecyclerView.ViewHolder
    {
        FragmentPiratePlaceImageBinding mBinding;
        private Bitmap mImageBitmap;

        public ImageHolder(FragmentPiratePlaceImageBinding binding)
        {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(Bitmap bitmap)
        {
            mImageBitmap = bitmap;
            mBinding.itemImageView.setImageBitmap(mImageBitmap);
        }
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageHolder>
    {
        List<File> mImageFiles;

        public ImageAdapter(List<File> imageFiles)
        {
            mImageFiles = imageFiles;
        }

        public void updateImageFiles(List<File> imageFiles)
        {
            mImageFiles = imageFiles;
        }

        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            FragmentPiratePlaceImageBinding binding = DataBindingUtil
                    .inflate(layoutInflater, R.layout.fragment_pirate_place_image,
                            parent, false);
            return new ImageHolder(binding);
        }

        @Override
        public void onBindViewHolder(ImageHolder holder, int position)
        {
            File photoFile = mImageFiles.get(position);
            Bitmap bitmap = null;
            if (! (photoFile == null || !photoFile.exists())) {
                bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
            }

            holder.bind(bitmap);
        }

        @Override
        public int getItemCount()
        {
            return mImageFiles.size();
        }
    }
}
