package net.osmand.plus.measurementtool;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.osmand.AndroidUtils;
import net.osmand.plus.ApplicationMode;
import net.osmand.plus.OsmandSettings;
import net.osmand.plus.R;
import net.osmand.plus.base.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class SnapToRoadBottomSheetDialogFragment extends BottomSheetDialogFragment {

	public static final String TAG = "SnapToRoadBottomSheetDialogFragment";

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final OsmandSettings settings = getMyApplication().getSettings();
		final boolean nightMode = getMyApplication().getDaynightHelper().isNightModeForMapControls();
		final int themeRes = nightMode ? R.style.OsmandDarkTheme : R.style.OsmandLightTheme;

		final View view = View.inflate(new ContextThemeWrapper(getContext(), themeRes), R.layout.fragment_snap_to_road_bottom_sheet_dialog, container);
		if (nightMode) {
			((TextView) view.findViewById(R.id.cancel_row_text))
					.setTextColor(ContextCompat.getColor(getActivity(), R.color.dashboard_general_button_text_dark));
		}
		view.findViewById(R.id.cancel_row).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});

		LinearLayout navContainer = (LinearLayout) view.findViewById(R.id.navigation_types_container);
		final List<ApplicationMode> modes = new ArrayList<>(ApplicationMode.values(settings));
		modes.remove(ApplicationMode.DEFAULT);
		for (ApplicationMode mode : modes) {
			View row = View.inflate(new ContextThemeWrapper(getContext(), themeRes), R.layout.list_item_icon_and_title, null);
			((ImageView) row.findViewById(R.id.icon)).setImageDrawable(getContentIcon(mode.getSmallIconDark()));
			((TextView) row.findViewById(R.id.title)).setText(mode.toHumanString(getContext()));
			navContainer.addView(row);
		}

		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		final int height = metrics.heightPixels;

		view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				final View scrollView = view.findViewById(R.id.navigation_types_scroll_view);
				int scrollViewHeight = scrollView.getHeight();
				int dividerHeight = AndroidUtils.dpToPx(getContext(), 1);
				int cancelButtonHeight = getContext().getResources().getDimensionPixelSize(R.dimen.snap_to_road_bottom_sheet_cancel_button_height);
				int spaceForScrollView = height - getStatusBarHeight() - getNavBarHeight() - dividerHeight - cancelButtonHeight;
				if (scrollViewHeight > spaceForScrollView) {
					scrollView.getLayoutParams().height = spaceForScrollView;
					scrollView.requestLayout();
				}

				ViewTreeObserver obs = view.getViewTreeObserver();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					obs.removeOnGlobalLayoutListener(this);
				} else {
					obs.removeGlobalOnLayoutListener(this);
				}
			}
		});

		return view;
	}

	private int getStatusBarHeight() {
		int statusBarHeight = 0;
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = getResources().getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
	}

	private int getNavBarHeight() {
		if (!hasNavBar()) {
			return 0;
		}
		boolean landscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		boolean isSmartphone = getResources().getConfiguration().smallestScreenWidthDp < 600;
		if (isSmartphone && landscape) {
			return 0;
		}
		int id = getResources().getIdentifier(landscape ? "navigation_bar_height_landscape" : "navigation_bar_height", "dimen", "android");
		if (id > 0) {
			return getResources().getDimensionPixelSize(id);
		}
		return 0;
	}

	private boolean hasNavBar() {
		int id = getResources().getIdentifier("config_showNavigationBar", "bool", "android");
		return id > 0 && getResources().getBoolean(id);
	}
}
