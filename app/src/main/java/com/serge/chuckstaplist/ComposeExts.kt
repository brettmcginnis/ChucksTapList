package com.serge.chuckstaplist

import android.content.res.Configuration

val Configuration.isLandscape get() = screenWidthDp > screenHeightDp
