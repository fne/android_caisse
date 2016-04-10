package fr.info.antillesinfov2.fragment.impl;

import android.os.Bundle;
import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.Constant;
import fr.info.antillesinfov2.fragment.AbstractNewsFragment;

public class AllSectionFragment extends AbstractNewsFragment {

	public AllSectionFragment() {
		super();
		Bundle args = new Bundle();
		args.putString(Constant.SECTION_NAME, "Tout");
		setArguments(args);
	}

	@Override
	protected void initProperties() {
		setIdView(R.layout.fragment_all_view);
		setIdListView(R.id.listViewAll);
		setUrl(DEFAULT_RSS);

	}
}
