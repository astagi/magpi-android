/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.themagpi.android;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;

public class CompatActionBarNavHandler implements TabListener, OnNavigationListener {

    CompatActionBarNavListener mNavListener;

    public CompatActionBarNavHandler(CompatActionBarNavListener listener) {
        mNavListener = listener;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        mNavListener.onCategorySelected(itemPosition);
        return true;
    }

    @Override
    public void onTabSelected(Tab tab,
            android.support.v4.app.FragmentTransaction ft) {
        mNavListener.onCategorySelected(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab,
            android.support.v4.app.FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(Tab tab,
            android.support.v4.app.FragmentTransaction ft) {
    }

}
