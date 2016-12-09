package io.edanni.money.ui.fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import io.edanni.money.R;
import io.edanni.money.domain.entity.Statement;
import io.edanni.money.domain.repository.StatementRepository;
import io.edanni.money.infrastructure.rest.Page;
import io.edanni.money.infrastructure.rest.RetrofitFactory;
import io.edanni.money.ui.list.StatementViewAdapter;
import io.edanni.money.ui.list.listener.EndlessScrollListener;
import org.androidannotations.annotations.*;
import org.androidannotations.api.BackgroundExecutor;

import java.io.IOException;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
@EFragment(R.layout.fragment_statement_list)
public class StatementListFragment extends Fragment
{
    @ViewById
    FrameLayout frame;
    @ViewById(R.id.list)
    RecyclerView statementList;
    @ViewById(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewById
    FloatingActionButton fab;
    @Bean
    StatementViewAdapter statementViewAdapter;
    @Bean
    RetrofitFactory retrofitFactory;
    StatementRepository statementRepository;
    private EndlessScrollListener scrollListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StatementListFragment()
    {
    }

    @AfterViews
    void afterViews()
    {
        getActivity().setTitle( getString( R.string.statements ) );
        statementRepository = retrofitFactory.createService( StatementRepository.class );
        statementList.setAdapter( statementViewAdapter );
        scrollListener = new EndlessScrollListener( (LinearLayoutManager) statementList.getLayoutManager() )
        {
            @Override
            public void onLoadMore( int page, int totalItemsCount, RecyclerView view )
            {
                getStatements( page );
            }
        };
        statementList.addOnScrollListener( scrollListener );
        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                getStatements( false );
            }
        } );
        swipeRefreshLayout.setRefreshing( true );
        getStatements( true );
    }

    @Background(id = "statements", serial = "statements")
    void getStatements( boolean showLoading )
    {
        try
        {
            Page<Statement> statements = statementRepository.getStatements( "past", 1 ).execute().body();
            showStatements( statements.content, showLoading );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    @Background(id = "statements", serial = "statements")
    void getStatements( int page )
    {
        try
        {
            Page<Statement> statements = statementRepository.getStatements( "past", page ).execute().body();
            addStatements( statements.content );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    @UiThread
    void showStatements( List<Statement> statements, boolean showLoading )
    {
        ((StatementViewAdapter) statementList.getAdapter()).setItems( statements );
        swipeRefreshLayout.setRefreshing( false );
        scrollListener.resetState();
    }

    @UiThread
    void addStatements( List<Statement> statements )
    {
        ((StatementViewAdapter) statementList.getAdapter()).getItems().addAll( statements );
        ((StatementViewAdapter) statementList.getAdapter()).notifyDataSetChanged();
    }

    @Click
    void fab()
    {
        BackgroundExecutor.cancelAll( "statements", true );
        StatementNewFragment dialog = new StatementNewFragment_();
        dialog.setTargetFragment( this, 300 );
        dialog.show( getActivity().getSupportFragmentManager(), "fragment_statement_new_dialog" );
    }
}
