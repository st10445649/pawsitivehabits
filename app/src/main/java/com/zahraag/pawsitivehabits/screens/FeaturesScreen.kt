package com.zahraag.pawsitivehabits.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zahraag.pawsitivehabits.data.FeatureItem
import com.zahraag.pawsitivehabits.data.featureItemsList
import com.zahraag.pawsitivehabits.ui.theme.MintBackground
import com.zahraag.pawsitivehabits.ui.theme.MintDarkGreen

@Composable
fun FeaturesScreen(
    onFeatureClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MintBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Features",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MintDarkGreen
        )

        Spacer(modifier = Modifier.height(6.dp))


        Text(
            text = "Select a feature below to manage your pets' wellness",
            fontSize = 14.sp,
            color = MintDarkGreen.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 20.dp)
        )


        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(featureItemsList) { feature ->
                FeatureCard(
                    feature = feature,
                    onClick = { onFeatureClick(feature.route) }
                )
            }
        }
    }
}

@Composable
fun FeatureCard(
    feature: FeatureItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = feature.backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .height(160.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Icon(
                painter = painterResource(id = feature.iconRes),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.25f),
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.Center)
            )


            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = feature.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 22.sp
                )

                Text(
                    text = feature.subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    lineHeight = 15.sp
                )
            }
        }
    }
}