package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SubscriptionPlan
import com.example.data.model.UserProfile
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BorderSubtleDark
import com.example.ui.theme.CosmicCardDark
import com.example.ui.theme.CosmicSurfaceDark
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.theme.VioletNeon

@Composable
fun SubscriptionScreen(
  userProfile: UserProfile,
  plans: List<SubscriptionPlan>,
  onSelectPlan: (SubscriptionPlan) -> Unit,
  checkoutPlan: SubscriptionPlan?,
  onCompleteCheckout: (String) -> Unit,
  onDismissCheckout: () -> Unit
) {
  var isCurrencyInr by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .testTag("subscription_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item { Spacer(modifier = Modifier.height(6.dp)) }

    // 90-Day Free Trial Spotlight Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF261905))
          .border(1.dp, AmberWarning, RoundedCornerShape(20.dp))
          .padding(18.dp)
          .testTag("trial_details_banner")
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(AmberWarning.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text("90-DAY COMPLIMENTARY TRIAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberWarning, letterSpacing = 1.sp)
                Text("${userProfile.trialDaysRemaining} Days Remaining", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AmberWarning)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("ACTIVE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Every eligible account receives 3 full months of Premium access. Renewal date: ${userProfile.trialEndDate}. You will be notified 7 days, 3 days, and 1 day prior. Cancel anytime without surprise charges.",
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = TextSecondaryDark
          )
        }
      }
    }

    // Currency Switcher
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("CHOOSE CLOUD TIER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, letterSpacing = 1.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          FilterChip(
            selected = !isCurrencyInr,
            onClick = { isCurrencyInr = false },
            label = { Text("USD ($)", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyanNeon, selectedLabelColor = Color.Black)
          )
          FilterChip(
            selected = isCurrencyInr,
            onClick = { isCurrencyInr = true },
            label = { Text("INR (₹)", fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyanNeon, selectedLabelColor = Color.Black)
          )
        }
      }
    }

    // Subscription Plans Cards
    items(plans, key = { it.id }) { plan ->
      val isCurrent = userProfile.currentPlanId == plan.id
      val priceText = if (plan.priceMonthlyUsd == 0.0) {
        "Free forever"
      } else if (isCurrencyInr) {
        "₹${plan.priceMonthlyInr.toInt()} / month"
      } else {
        "$${plan.priceMonthlyUsd} / month"
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(CosmicCardDark)
          .border(
            width = if (plan.isPopular) 2.dp else 1.dp,
            color = if (plan.isPopular) CyanNeon else BorderSubtleDark,
            shape = RoundedCornerShape(20.dp)
          )
          .padding(18.dp)
          .testTag("plan_card_${plan.id.lowercase()}")
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column {
              Text(plan.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
              Text(plan.tagline, fontSize = 12.sp, color = TextSecondaryDark)
            }

            if (plan.isPopular) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(CyanNeon)
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text("MOST POPULAR", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(priceText, fontSize = 20.sp, fontWeight = FontWeight.Black, color = CyanNeon)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (plan.isUnlimited) "Unlimited Fair-Use" else "${plan.storageGb} GB Storage",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = EmeraldSuccess
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            plan.features.forEach { feat ->
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(feat, fontSize = 12.sp, color = TextSecondaryDark)
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = { onSelectPlan(plan) },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isCurrent) Color(0x3300F0FF) else CyanNeon,
              contentColor = if (isCurrent) CyanNeon else Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("select_plan_${plan.id.lowercase()}")
          ) {
            Text(if (isCurrent) "Current Active Plan" else "Subscribe / Switch", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    item { Spacer(modifier = Modifier.height(20.dp)) }
  }

  // Payment Gateway Modal
  if (checkoutPlan != null) {
    PaymentGatewayDialog(
      plan = checkoutPlan,
      isCurrencyInr = isCurrencyInr,
      onDismiss = onDismissCheckout,
      onConfirm = { onCompleteCheckout(checkoutPlan.id) }
    )
  }
}

@Composable
fun PaymentGatewayDialog(
  plan: SubscriptionPlan,
  isCurrencyInr: Boolean,
  onDismiss: () -> Unit,
  onConfirm: () -> Unit
) {
  var selectedPaymentMethod by remember { mutableStateOf("UPI") } // UPI, CARD, NETBANKING
  var upiId by remember { mutableStateOf("alex@okhdfcbank") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = CosmicSurfaceDark,
      border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleDark),
      modifier = Modifier.fillMaxWidth().testTag("payment_gateway_dialog")
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0x3310B981)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Receipt, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(20.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text("Secure Nexora Checkout", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
            Text(
              text = if (isCurrencyInr) "₹${plan.priceMonthlyInr.toInt()}/mo • ${plan.name}" else "$${plan.priceMonthlyUsd}/mo • ${plan.name}",
              fontSize = 12.sp,
              color = CyanNeon
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Select Payment Gateway", fontSize = 12.sp, color = TextSecondaryDark)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          listOf("UPI", "CARD", "NETBANKING").forEach { method ->
            FilterChip(
              selected = selectedPaymentMethod == method,
              onClick = { selectedPaymentMethod = method },
              label = { Text(method, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CyanNeon, selectedLabelColor = Color.Black)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedPaymentMethod == "UPI") {
          Text("Supported: Google Pay, PhonePe, Paytm, BHIM", fontSize = 11.sp, color = TextSecondaryDark)
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = upiId,
            onValueChange = { upiId = it },
            placeholder = { Text("Enter UPI ID (e.g., name@upi)", color = TextSecondaryDark) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark, focusedBorderColor = CyanNeon)
          )
        } else if (selectedPaymentMethod == "CARD") {
          Text("Credit & Debit Cards (Visa, Mastercard, RuPay)", fontSize = 11.sp, color = TextSecondaryDark)
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = "•••• •••• •••• 4242",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimaryDark, unfocusedTextColor = TextPrimaryDark, focusedBorderColor = CyanNeon)
          )
        } else {
          Text("Direct Net Banking: HDFC, ICICI, SBI, Axis, Chase, Citi", fontSize = 11.sp, color = TextSecondaryDark)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 90-day Free Trial Grace Notice
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x22F59E0B))
            .padding(10.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Complimentary 90-day trial applies. Billed only after trial ends.", fontSize = 11.sp, color = AmberWarning)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
          Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
            Text("Cancel", color = TextSecondaryDark)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = onConfirm,
            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("confirm_payment_button")
          ) {
            Text("Authorize & Start", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
