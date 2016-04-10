package fr.info.antillesinfov2.fragment.impl;

import android.os.Bundle;
import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.Constant;
import fr.info.antillesinfov2.fragment.AbstractNewsFragment;

public class MartiniqueSectionFragment extends AbstractNewsFragment {

	public MartiniqueSectionFragment() {
		super();
		Bundle args = new Bundle();
		args.putString(Constant.SECTION_NAME, "Mada");
		setArguments(args);
	}

	@Override
	protected void initProperties() {
		setIdView(R.layout.fragment_martinique_view);
		setIdListView(R.id.listViewMartinique);
		setUrl(MQ_RSS);		
	}
}
