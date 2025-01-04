
package tn.esprit.smartspend.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.scale

@Composable
fun PieChartView(
    data: List<Double>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val total = data.sum()
    val angles = data.map { (it / total) * 360f }
    val colors = List(data.size) { Color((0xFF000000..0xFFFFFFFF).random()) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        ) {
            var startAngle = 0f
            angles.forEachIndexed { index, sweepAngle ->
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle.toFloat(),
                    useCenter = true,
                    size = Size(size.minDimension, size.minDimension)
                )
                startAngle += sweepAngle.toFloat()
            }
        }

        // Legend
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            data.zip(labels).zip(colors).forEach { (pair, color) ->
                val (value, label) = pair
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$label: ${"%.2f".format(value)}")
                }
            }
        }
    }
}

@Composable
fun BarChartView(
    data: List<Double>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val maxValue = data.maxOrNull() ?: 0.0
    val colors = List(data.size) { Color((0xFF000000..0xFFFFFFFF).random()) }

    Column(
        modifier = modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            data.zip(labels).zip(colors).forEach { (pair, color) ->
                val (value, label) = pair
                val height = ((value / maxValue) * 200).dp

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${"%.1f".format(value)}",
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .width(30.dp)
                            .height(height)
                            .background(color)
                    )
                    Text(
                        text = label,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}