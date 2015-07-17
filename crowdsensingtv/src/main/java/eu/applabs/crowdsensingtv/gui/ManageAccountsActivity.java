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
import eu.applabs.crowdsensinglibrary.ILibraryResultListener;
import eu.applabs.crowdsensinglibrary.Library;
import eu.applabs.crowdsensinglibrary.data.Command;
import eu.applabs.crowdsensinglibrary.data.Poll;
import eu.applabs.crowdsensinglibrary.gui.LoginDialog;
import eu.applabs.crowdsensingtv.R;
import eu.applabs.crowdsensingtv.data.FitnessAccount;
import eu.applabs.crowdsensingtv.data.LibraryAccount;
import eu.applabs.crowdsensingtv.presenter.FitnessAccountPresenter;
import eu.applabs.crowdsensingtv.presenter.LibraryAccountPresenter;

public class ManageAccountsActivity extends Activity implements
        OnItemViewClickedListener,
        LoginDialog.ILoginDialogListener,
        FitnessLibrary.IFitnessLibraryListener,
        ILibraryResultListener {

    private static final String sClassName = ManageAccountsActivity.class.getSimpleName();

    private FragmentManager mFragmentManager = null;
    private BrowseFragment mBrowseFragment = null;
    private ArrayObjectAdapter mArrayObjectAdapter = null;

    private Library mLibrary = null;
    private FitnessLibrary mFitnessLibrary = null;

    private ManageAccountsActivity mActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manageaccounts);

        mLibrary = Library.getInstance();
        mLibrary.registerListener(this);
        mFitnessLibrary = FitnessLibrary.getInstance();
        mFitnessLibrary.registerListener(this);

        mFragmentManager = getFragmentManager();
        mBrowseFragment = (BrowseFragment) mFragmentManager.findFragmentById(R.id.id_Fragment_ManageAccountActivity);

        mBrowseFragment.setHeadersState(BrowseFragment.HEADERS_ENABLED);
        mBrowseFragment.setBadgeDrawable(getDrawable(R.drawable.browse_logo));
        mBrowseFragment.setOnItemViewClickedListener(this);

        BackgroundManager backgroundManager = BackgroundManager.getInstance(this);
        backgroundManager.attach(this.getWindow());
        backgroundManager.setDrawable(getResources().getDrawable(R.drawable.background, null));

        mArrayObjectAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        mBrowseFragment.setAdapter(mArrayObjectAdapter);

        updateUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mActivity = this;
    }

    @Override
    protected void onStop() {
        super.onStop();

        mActivity = null;
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

                HeaderItem accountHeader = new HeaderItem(getString(R.string.ManageAccountActivity_Header_Account));
                mArrayObjectAdapter.add(new ListRow(accountHeader, accountAdapter));

                // Portals
                ArrayObjectAdapter connectedPortalsAdapter = new ArrayObjectAdapter(new FitnessAccountPresenter());
                HeaderItem connectedPortalsHeader = new HeaderItem(getString(R.string.ManageAccountActivity_Header_ConnectedPortals));

                ArrayObjectAdapter availablePortalsAdapter = new ArrayObjectAdapter(new FitnessAccountPresenter());
                HeaderItem availablePortalsHeader = new HeaderItem(getString(R.string.ManageAccountActivity_Header_AvailablePortals));

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
                        .setTitle(R.string.ManageAccountActivity_LogoutDialog_Title)
                        .setMessage(R.string.ManageAccountActivity_LogoutDialog_Message)
                        .setPositiveButton(R.string.ManageAccountActivity_LogoutDialog_Button_Ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mLibrary.logout();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.ManageAccountActivity_LogoutDialog_Button_Cancel, new DialogInterface.OnClickListener() {
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
                        .setTitle(R.string.ManageAccountActivity_LogoutDialog_Title)
                        .setMessage(R.string.ManageAccountActivity_LogoutDialog_Message)
                        .setPositiveButton(R.string.ManageAccountActivity_LogoutDialog_Button_Ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mFitnessLibrary.disconnect(account.getType());
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.ManageAccountActivity_LogoutDialog_Button_Cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            } else if (mFitnessLibrary!= null && !mFitnessLibrary.isConnected(account.getType())) {
                // Login
                if(account.getType() != Portal.PortalType.Google) {
                    Toast.makeText(this, R.string.ManageAccountActivity_Toast_NotSupported, Toast.LENGTH_SHORT).show();
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
        mLibrary.loadCommands(MainActivity.START_URL, sClassName);
    }

    @Override
    public void onPersonReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus status, int requestId, Person person) {
        // Not needed
    }

    @Override
    public void onStepsReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus status, int requestId, List<StepBucket> list) {
        // Not needed
    }

    @Override
    public void onActivitiesReceived(FitnessLibrary.IFitnessLibraryListener.ExecutionStatus status, int requestId, List<ActivityBucket> list) {
        // Not needed
    }

    @Override
    public void onPortalConnectionStateChanged() {
        updateUI();
    }

    @Override
    public void onLibraryResult(final ILibraryResultListener.ExecutionStatus status, final Poll poll, final String className) {

    }

    @Override
    public void onLibraryResult(final ILibraryResultListener.ExecutionStatus status, final List<Command> list, final String className) {
        if(className.compareTo(sClassName) == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (status != ILibraryResultListener.ExecutionStatus.Success) {
                        Toast.makeText(mActivity, R.string.ManageAccountActivity_Toast_LoginError, Toast.LENGTH_SHORT).show();
                        mLibrary.logout();

                        // Login
                        LoginDialog dialog = new LoginDialog(mActivity);
                        dialog.registerListener(mActivity);
                        dialog.show();
                    }
                }
            });
        }
    }
}
