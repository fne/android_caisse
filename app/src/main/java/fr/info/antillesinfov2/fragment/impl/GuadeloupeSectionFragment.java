package fr.info.antillesinfov2.fragment.impl;

import android.os.Bundle;
import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.Constant;
import fr.info.antillesinfov2.fragment.AbstractNewsFragment;

public class GuadeloupeSectionFragment extends AbstractNewsFragment {

	public GuadeloupeSectionFragment() {
		super();
		Bundle args = new Bundle();
        args.putString(Constant.SECTION_NAME, "Gwada");
        setArguments(args);
	}

	@Override
	protected void initProperties() {		
		setIdView(R.layout.fragment_guadeloupe_view);
		setIdListView(R.id.listViewGuadeloupe);
		setUrl(GP_RSS);
	}	
}
