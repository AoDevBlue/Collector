package net.aohayou.collector.collectiondetail;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.aohayou.collector.R;
import net.aohayou.collector.collectiondetail.view.FormulaAdapter;
import net.aohayou.collector.data.formula.Formula;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CollectionDetailFragment extends Fragment implements CollectionDetailContract.View {

    private static final String TAG_EDIT_FORMULA = "editFormula";

    @BindView(R.id.toolbar) Toolbar toolbar;
    private FormulaAdapter formulaAdapter;

    private CollectionDetailContract.Presenter presenter;

    public CollectionDetailFragment() {
        // Required empty public constructor
    }

    public static CollectionDetailFragment newInstance() {
        return new CollectionDetailFragment();
    }

    @Override
    public void setPresenter(CollectionDetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        formulaAdapter = new FormulaAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection_detail, container, false);

        View recyclerView = view.findViewById(R.id.list);
        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView formulaRecyclerView = (RecyclerView) recyclerView;

            GridLayoutManager layoutManager = new GridLayoutManager(context, 8);
            formulaRecyclerView.setLayoutManager(layoutManager);
            formulaRecyclerView.setAdapter(formulaAdapter);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();

        bindDialogs();
    }

    private void bindDialogs() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        EditFormulaDialogFragment creationFragment;
        creationFragment =
                (EditFormulaDialogFragment) fragmentManager.findFragmentByTag(TAG_EDIT_FORMULA);
        if (creationFragment != null) {
            creationFragment.setDialogListener(getCreationDialogListener());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getActivity());
    }

    @Override
    public void displayFormula(@NonNull Formula formula) {
        displayElementCount(formula.getElementCount());
        displayElements(formula);
    }

    private void displayElementCount(int count) {
        Resources res = getResources();
        String countString = res.getQuantityString(R.plurals.numberOfItems, count, count);
        if (toolbar != null) {
            toolbar.setSubtitle(countString);
        }
    }

    private void displayElements(@NonNull Formula formula) {
        formulaAdapter.replaceData(formula);
    }

    @Override
    public void displayCollectionName(@NonNull String collectionName) {
        if (toolbar != null) {
            toolbar.setTitle(collectionName);
        }
    }

    @Override
    public void showEditFormulaDialog(@NonNull String oldFormulaString) {
        EditFormulaDialogFragment editFormulaDialog;
        editFormulaDialog = EditFormulaDialogFragment.getInstance(oldFormulaString);
        editFormulaDialog.setDialogListener(getCreationDialogListener());
        editFormulaDialog.show(getActivity().getSupportFragmentManager(), TAG_EDIT_FORMULA);
    }

    private EditFormulaDialogFragment.Listener getCreationDialogListener() {
        return new EditFormulaDialogFragment.Listener() {
            @Override
            public void onCancel() {
                presenter.onFormulaEditCancel();
            }

            @Override
            public void onFormulaSet(@NonNull String newFormulaString) {
                presenter.onFormulaEdit(newFormulaString);
            }
        };
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
