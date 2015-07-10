package eu.applabs.crowdsensingtv.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.widget.Toast;

import java.util.List;

import eu.applabs.crowdsensingfitnesslibrary.FitnessLibrary;
import eu.applabs.crowdsensingfitnesslibrary.data.ActivityBucket;
import eu.applabs.crowdsensingfitnesslibrary.data.Person;
import eu.applabs.crowdsensingfitnesslibrary.data.StepBucket;
import eu.applabs.crowdsensingfitnesslibrary.portal.Portal;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.gui.LoginDialog;
import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingtv.data.FitnessAccount;
import eu.applabs.crowdsensingtv.data.LibraryAccount;
import eu.applabs.crowdsensingtv.presenter.FitnessAccountPresenter;
import eu.applabs.crowdsensingtv.presenter.LibraryAccountPresenter;

public class ManageAccountsActivity extends Activity implements OnItemViewClickedListener,
        LoginDialog.ILoginDialogListener, FitnessLibrary.IFitnessLibraryListener {

    private FragmentManager mFragmentManager = null;
    private BrowseFragment mBrowseFragment = null;
    private ArrayObjectAdapter mArrayObjectAdapter = null;

    private Library mLibrary = null;
    private FitnessLibrary mFitnessLibrary = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manageaccounts);

        mLibrary = Library.getInstance();
        mFitnessLibrary = FitnessLibrary.getInstance();
        mFitnessLibrary.registerListener(this);

        mFragmentManager = getFragmentManager();
        mBrowseFragment = (BrowseFragment) mFragmentManager.findFragmentById(R.id.id_Fragment_ManageAccountActivity);

        mBrowseFragment.setHeadersState(BrowseFragment.HEADERS_ENABLED);
        mBrowseFragment.setTitle("CrowdSensingTV");
        mBrowseFragment.setBadgeDrawable(getDrawable(R.drawable.unilogoandlabel));
        mBrowseFragment.setOnItemViewClickedListener(this);

        BackgroundManager backgroundManager = BackgroundManager.getInstance(this);
        backgroundManager.attach(this.getWindow());
        backgroundManager.setDrawable(getResources().getDrawable(R.drawable.poll, null));

        mArrayObjectAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        mBrowseFragment.setAdapter(mArrayObjectAdapter);

        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(mFitnessLibrary != null) {
            mFitnessLibrary.checkActivityResult(requestCode, resultCode, data);
        }
    }

    public void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mArrayObjectAdapter.clear();

                // LibraryAccount
                ArrayObjectAdapter accountAdapter = new ArrayObjectAdapter(new LibraryAccountPresenter());
                LibraryAccount libraryAccount = new LibraryAccount(getApplicationContext());

                accountAdapter.add(libraryAccount);

                HeaderItem accountHeader = new HeaderItem("LibraryAccount");
                mArrayObjectAdapter.add(new ListRow(accountHeader, accountAdapter));

                // Portals
                ArrayObjectAdapter connectedPortalsAdapter = new ArrayObjectAdapter(new FitnessAccountPresenter());
                HeaderItem connectedPortalsHeader = new HeaderItem("Connected portals");

                ArrayObjectAdapter availablePortalsAdapter = new ArrayObjectAdapter(new FitnessAccountPresenter());
                HeaderItem availablePortalsHeader = new HeaderItem("Available portals");

                if(mFitnessLibrary != null) {
                    // Check Google
                    FitnessAccount googlefit = new FitnessAccount(getApplicationContext());
                    googlefit.setType(Portal.PortalType.Google);

                    if(mFitnessLibrary.isConnected(Portal.PortalType.Google)) {
                        connectedPortalsAdapter.add(googlefit);
                    } else {
                        availablePortalsAdapter.add(googlefit);
                    }

                    // Check Apple
                    FitnessAccount applehealthkit = new FitnessAccount(getApplicationContext());
                    applehealthkit.setType(Portal.PortalType.Apple);

                    if(mFitnessLibrary.isConnected(Portal.PortalType.Apple)) {
                        connectedPortalsAdapter.add(applehealthkit);
                    } else {
                        availablePortalsAdapter.add(applehealthkit);
                    }

                    // Check Microsoft
                    FitnessAccount microsofthealthvault = new FitnessAccount(getApplicationContext());
                    microsofthealthvault.setType(Portal.PortalType.Microsoft);

                    if(mFitnessLibrary.isConnected(Portal.PortalType.Microsoft)) {
                        connectedPortalsAdapter.add(microsofthealthvault);
                    } else {
                        availablePortalsAdapter.add(microsofthealthvault);
                    }

                }

                mArrayObjectAdapter.add(new ListRow(connectedPortalsHeader, connectedPortalsAdapter));
                mArrayObjectAdapter.add(new ListRow(availablePortalsHeader, availablePortalsAdapter));
            }
        });
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if(item instanceof LibraryAccount) {
            if(mLibrary != null && mLibrary.accountAvailable()) {
                // Logout
                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Do you like to logout the current user?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mLibrary.logout();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            } else {
                // Login
                LoginDialog dialog = new LoginDialog(this);
                dialog.registerListener(this);
                dialog.show();
            }
        } else if(item instanceof FitnessAccount) {
            final FitnessAccount account = (FitnessAccount) item;

            if(mFitnessLibrary != null && mFitnessLibrary.isConnected(account.getType())) {
                // Logout
                new AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Do you like to logout the current user?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mFitnessLibrary.disconnect(account.getType());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            } else if (mFitnessLibrary!= null && !mFitnessLibrary.isConnected(account.getType())) {
                // Login
                if(account.getType() != Portal.PortalType.Google) {
                    Toast.makeText(this, "Currently not supported", Toast.LENGTH_SHORT).show();
                } else {
                    mFitnessLibrary.connect(account.getType(), this);
                }
            } else {

            }
        }
    }

    @Override
    public void onLoginCanceled() {

    }

    @Override
    public void onLoginSaved() {

    }

    @Override
    public void onPersonReceived(Person person) {
        // Not needed
    }

    @Override
    public void onStepsReceived(List<StepBucket> list) {
        // Not needed
    }

    @Override
    public void onActivitiesReceived(List<ActivityBucket> list) {
        // Not needed
    }

    @Override
    public void onPortalConnectionStateChanged() {
        updateUI();
    }
}
