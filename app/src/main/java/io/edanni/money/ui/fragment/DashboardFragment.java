package io.edanni.money.ui.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.Toast;
import io.edanni.money.R;
import io.edanni.money.domain.entity.Credit;
import io.edanni.money.domain.entity.Debit;
import io.edanni.money.infrastructure.security.CredentialsStore;
import io.edanni.money.ui.activity.LoginActivity_;
import io.edanni.money.ui.activity.MainActivity;
import org.androidannotations.annotations.*;

/**
 * Created by eduardo on 09/12/16.
 */
@EFragment(R.layout.fragment_dashboard)
@OptionsMenu(R.menu.main)
public class DashboardFragment extends Fragment
{
    @ViewById
    Button newCredit;
    @ViewById
    Button newDebit;
    @Bean
    CredentialsStore store;

    @AfterViews
    void afterViews()
    {
        getActivity().setTitle( "Money" );
    }

    @OptionsItem(R.id.main_logout)
    void logout()
    {
        showLogout();
    }

    @UiThread
    void showLogout()
    {
        store.setEmail( null );
        store.setPassword( null );
        startActivity( new Intent( getContext(), LoginActivity_.class ) );
        Toast.makeText( getActivity(), getString( R.string.sign_out_successful ), Toast.LENGTH_SHORT ).show();
        getActivity().finish();
    }

    @Click
    void newCredit()
    {
        NewStatementFragment fragment = NewStatementFragment.newInstance( new Credit() );
        ((MainActivity) getActivity()).changeToFragment( fragment );
    }

    @Click
    void newDebit()
    {
        NewStatementFragment fragment = NewStatementFragment.newInstance( new Debit() );
        ((MainActivity) getActivity()).changeToFragment( fragment );
    }
}
