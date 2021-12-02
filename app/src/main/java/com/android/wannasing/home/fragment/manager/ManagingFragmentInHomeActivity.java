package com.android.wannasing.home.fragment.manager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.wannasing.R;
import com.android.wannasing.databinding.ActivityManagingFragmentInHomeBinding;
import com.android.wannasing.db.entity.Party;
import com.android.wannasing.db.entity.User;
import com.android.wannasing.home.fragment.map.ManagingMapFragment;
import com.android.wannasing.home.fragment.myprofile.showing.ShowingMyProfileFragment;
import com.android.wannasing.home.fragment.partygroup.ShowingPartyGroupInHomeFragment;
import com.android.wannasing.party.showinginfo.ShowingPartyInfoActivity;

public class ManagingFragmentInHomeActivity extends AppCompatActivity {

  private ManagingMapFragment managingMapFragment;
  private ShowingPartyGroupInHomeFragment showingPartyGroupInHomeFragment;
  private ShowingMyProfileFragment showingMyProfileFragment;
  private FragmentManager fragmentManager;

  private ActivityManagingFragmentInHomeBinding binding;
  private User user;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityManagingFragmentInHomeBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    getUserInfoFromLoginActivity();
    fragmentManager = getSupportFragmentManager();
    showingPartyGroupInHomeFragment = ShowingPartyGroupInHomeFragment.newInstance();
    // 첫 화면 지정
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(R.id.manageFragments_fcv_fragmentContainer, showingPartyGroupInHomeFragment)
        .commitAllowingStateLoss();
    setBottomNav();

    getSupportFragmentManager()
        .setFragmentResultListener("PARTY_GROUP", this, (requestKey, result) -> {
          Party party = (Party) result.getSerializable("PARTY_INFO");
          Intent intent = new Intent(this, ShowingPartyInfoActivity.class);
          Bundle bundle = new Bundle();
          bundle.putSerializable("SELECTED_PARTY", party);
          intent.putExtras(bundle);
          startActivity(intent);
        });
  }

  private void getUserInfoFromLoginActivity() {
    this.user = (User) getIntent().getSerializableExtra("USER_INFO");
  }

  private void setBottomNav() {
    // bottomNavigationView의 아이템이 선택될 때 호출될 리스너 등록
    binding.manageFragmentsBnvBottomNavi.setOnItemSelectedListener(
        item -> {
          switch (item.getItemId()) {
            case R.id.home_nav:
              showFragment(FragmentType.SHOW_PARTY_GROUP);
              break;
            case R.id.map_nav:
              showFragment(FragmentType.MANAGE_MAP);
              break;
            case R.id.prof_nav:
              showFragment(FragmentType.SHOW_MY_PROFILE);
              break;
          }
          return true;
        }
    );
  }

  private void showFragment(FragmentType fragmentType) {
    switch (fragmentType) {
      case SHOW_PARTY_GROUP:
        if (showingPartyGroupInHomeFragment == null) {
          showingPartyGroupInHomeFragment = ShowingPartyGroupInHomeFragment.newInstance();
          fragmentManager.beginTransaction()
              .add(R.id.manageFragments_fcv_fragmentContainer, showingPartyGroupInHomeFragment)
              .commit();
        }

        if (showingPartyGroupInHomeFragment != null) {
          fragmentManager.beginTransaction().show(showingPartyGroupInHomeFragment).commit();
        }
        if (managingMapFragment != null) {
          fragmentManager.beginTransaction().hide(managingMapFragment).commit();
        }
        if (showingMyProfileFragment != null) {
          fragmentManager.beginTransaction().hide(showingMyProfileFragment).commit();
        }
        break;
      case MANAGE_MAP:
        if (managingMapFragment == null) {
          managingMapFragment = ManagingMapFragment.newInstance();
          fragmentManager.beginTransaction()
              .add(R.id.manageFragments_fcv_fragmentContainer, managingMapFragment)
              .commit();
        }

        if (showingPartyGroupInHomeFragment != null) {
          fragmentManager.beginTransaction().hide(showingPartyGroupInHomeFragment).commit();
        }
        if (managingMapFragment != null) {
          fragmentManager.beginTransaction().show(managingMapFragment).commit();
        }
        if (showingMyProfileFragment != null) {
          fragmentManager.beginTransaction().hide(showingMyProfileFragment).commit();
        }
        break;
      case SHOW_MY_PROFILE:
        if (showingMyProfileFragment == null) {
          showingMyProfileFragment = ShowingMyProfileFragment.newInstance(user);
          fragmentManager.beginTransaction()
              .add(R.id.manageFragments_fcv_fragmentContainer, showingMyProfileFragment)
              .commit();
        }

        if (showingPartyGroupInHomeFragment != null) {
          fragmentManager.beginTransaction().hide(showingPartyGroupInHomeFragment).commit();
        }
        if (managingMapFragment != null) {
          fragmentManager.beginTransaction().hide(managingMapFragment).commit();
        }
        if (showingMyProfileFragment != null) {
          fragmentManager.beginTransaction().show(showingMyProfileFragment).commit();
        }
        break;
    }
  }


  public enum FragmentType {
    SHOW_PARTY_GROUP, MANAGE_MAP, SHOW_MY_PROFILE
  }

}
