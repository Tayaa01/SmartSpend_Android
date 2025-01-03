package tn.esprit.smartspend.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun LinearProgressBar(
    value: Double,
    total: Double,
    color: Color,
    backgroundColor: Color = Color.Gray.copy(alpha = 0.2f),
    height: Float = 20f
) {
    val progress = if (total > 0) (value / total).toFloat() else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp)
    ) {
        // Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = backgroundColor,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(height)
            )
        }
        
        // Progress
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(height.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(height.dp)
                )
        )

        // Percentage text
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "${(progress * 100).toInt()}%",
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun CircularProgressBar(
    amount: Double,
    totalAmount: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    val percentage = if (totalAmount > 0) (amount / totalAmount).toFloat() else 0f

    Box(
        modifier = modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = color.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx())
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = percentage * 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${(percentage * 100).toInt()}%",
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}