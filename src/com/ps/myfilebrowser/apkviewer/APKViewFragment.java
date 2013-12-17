package com.ps.myfilebrowser.apkviewer;

import java.io.File;

import com.ps.myfilebrowser.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class APKViewFragment extends Fragment {
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment VideosFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static APKViewFragment newInstance(String param1, String param2) {
		APKViewFragment fragment = new APKViewFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	public APKViewFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		View toRet = inflater.inflate(R.layout.apk_view_fragment, container, false);
		
		parseAPKAndUpdateUI(toRet);
		
		return toRet; 
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	private void parseAPKAndUpdateUI(View view) {
		
		PackageManager pm = getActivity().getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(APKViewer.apkPath, 0);
		
		TextView tv0 = (TextView)view.findViewById(R.id.apk_file_name);
		File tmp = new File(APKViewer.apkPath);
		tv0.setText(tmp.getName());

		TextView tv1 = (TextView)view.findViewById(R.id.apk_app_name);
		tv1.setText(info.applicationInfo.name);
		
		TextView tv2 = (TextView)view.findViewById(R.id.apk_app_version);
		tv2.setText( " " + info.versionName + " / " + info.versionCode);
		
		TextView tv3 = (TextView)view.findViewById(R.id.apk_pkg_name);
		tv3.setText(info.packageName);
		APKViewer.packageName = info.packageName;

		TextView tv4 = (TextView)view.findViewById(R.id.apk_install_status);
		Button installB = (Button)view.findViewById(R.id.apk_install_button);
		
		Button runB = (Button)view.findViewById(R.id.apk_run_button);
		
		if (checkIfLauncherActivity()) {
			
		} else {
			runB.setEnabled(false);
		}

		if (isAPKInstalled(info.packageName, pm)) {
			
			APKViewer.install_status = true;
			int installedAppVerCode = getVersionCode(info.packageName, pm);
			
			tv4.setText(getActivity().getResources().getString(
					R.string.apkview_app_status_app_found) + " Version : " 
					+ installedAppVerCode);
			
			if (installedAppVerCode >= info.versionCode) {
				installB.setText(R.string.apkview_app_txt_app_uninstall);
			} else {
				installB.setText(R.string.apkview_app_txt_app_update);
				APKViewer.install_status = false; // new version is not installed
			}
			
		} else {
			tv4.setText(R.string.apkview_app_status_app_not_found);
			installB.setText(R.string.apkview_app_txt_app_install);
			APKViewer.install_status = false;
		}
	}
	
	private boolean checkIfLauncherActivity() {
		try {
			Intent targetIntent = getActivity().getPackageManager().getLaunchIntentForPackage(APKViewer.packageName);
			if (targetIntent != null)
				return true;
		}catch (Exception ex) {
			
		}
		return false;

		
	}
	
	private boolean isAPKInstalled(String pkg, PackageManager pm) {
	        boolean appInstalled = false;
            try
            {
                   pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES);
                   appInstalled = true;
            }
            catch (PackageManager.NameNotFoundException e)
            {
                try {
					pm.getPackageInfo(pkg, PackageManager.GET_SERVICES);
					appInstalled = true;
				} catch (PackageManager.NameNotFoundException e1) {
					// TODO Auto-generated catch block
//					e1.printStackTrace();
					try {
						pm.getPackageInfo(pkg, PackageManager.GET_PROVIDERS);
						appInstalled = true;
					} catch (NameNotFoundException e2) {
						// TODO Auto-generated catch block
//						e2.printStackTrace();
						try {
							pm.getPackageInfo(pkg, PackageManager.GET_RECEIVERS);
							appInstalled = true;
						} catch (NameNotFoundException e3) {
							// TODO Auto-generated catch block
						}
					}

				}
            }
            return appInstalled ;
    }
	
	private int getVersionCode(String pkg, PackageManager pm) {
		
		try {
			PackageInfo pf = pm.getPackageInfo(pkg,0);
			return pf.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
}