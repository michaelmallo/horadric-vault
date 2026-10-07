package com.horadricvault.feature.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.horadricvault.core.model.*
import com.horadricvault.feature.statcheck.VaultDesignTokens

@Composable
fun SanctuaryMapCanvas(
    region: SanctuaryRegion,
    nodes: List<MapNode>,
    completedNodeIds: Set<String>,
    visibleTypes: Set<MapNodeType>,
    onToggleNodeCompletion: (MapNode) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var selectedNode by remember { mutableStateOf<MapNode?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(VaultDesignTokens.AbyssalBlack)
            .border(1.dp, VaultDesignTokens.CharcoalBorder, RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.7f, 3.5f)
                    offset = Offset(
                        x = (offset.x + pan.x).coerceIn(-600f * scale, 600f * scale),
                        y = (offset.y + pan.y).coerceIn(-600f * scale, 600f * scale)
                    )
                }
            }
            .pointerInput(nodes, visibleTypes, scale, offset) {
                detectTapGestures { tapOffset ->
                    val clicked = nodes.filter { visibleTypes.contains(it.type) }.find { node ->
                        val nodeScreenX = (node.normalizedX * size.width) * scale + offset.x
                        val nodeScreenY = (node.normalizedY * size.height) * scale + offset.y
                        val dist = (tapOffset.x - nodeScreenX) * (tapOffset.x - nodeScreenX) +
                                (tapOffset.y - nodeScreenY) * (tapOffset.y - nodeScreenY)
                        dist <= (24f * scale) * (24f * scale)
                    }
                    selectedNode = clicked
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // 1. Draw Sanctuary Cartography Grid & Contour Rings
            val gridStep = 80f * scale
            val gridColor = Color(0xFF1E2028).copy(alpha = 0.5f)

            var x = (offset.x % gridStep)
            while (x < canvasW) {
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, canvasH),
                    strokeWidth = 1f
                )
                x += gridStep
            }

            var y = (offset.y % gridStep)
            while (y < canvasH) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(canvasW, y),
                    strokeWidth = 1f
                )
                y += gridStep
            }

            // Region Decorative Contour Ellipse
            val centerX = (canvasW * 0.5f) * scale + offset.x
            val centerY = (canvasH * 0.5f) * scale + offset.y
            drawCircle(
                color = VaultDesignTokens.EmberRed.copy(alpha = 0.08f),
                radius = 280f * scale,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2f)
            )

            // 2. Draw Filtered Nodes
            nodes.filter { visibleTypes.contains(it.type) }.forEach { node ->
                val nodeX = (node.normalizedX * canvasW) * scale + offset.x
                val nodeY = (node.normalizedY * canvasH) * scale + offset.y
                val isCompleted = completedNodeIds.contains(node.nodeId)
                val baseColor = Color(node.type.colorHex)

                val nodeRadius = (if (node == selectedNode) 11f else 8f) * scale

                if (isCompleted) {
                    // Completed node: Dimmed with subtle rune ring
                    drawCircle(
                        color = baseColor.copy(alpha = 0.35f),
                        radius = nodeRadius,
                        center = Offset(nodeX, nodeY)
                    )
                    drawCircle(
                        color = VaultDesignTokens.RunicTeal,
                        radius = nodeRadius * 0.5f,
                        center = Offset(nodeX, nodeY)
                    )
                } else {
                    // Uncompleted node: Vibrant glowing marker
                    drawCircle(
                        color = baseColor.copy(alpha = 0.25f),
                        radius = nodeRadius * 1.6f,
                        center = Offset(nodeX, nodeY)
                    )
                    drawCircle(
                        color = baseColor,
                        radius = nodeRadius,
                        center = Offset(nodeX, nodeY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = nodeRadius * 0.35f,
                        center = Offset(nodeX, nodeY)
                    )
                }
            }
        }

        // Floating Node Detail Banner (when tapped)
        selectedNode?.let { node ->
            val isCompleted = completedNodeIds.contains(node.nodeId)
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = VaultDesignTokens.SlateIron),
                shape = RoundedCornerShape(8.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(node.type.colorHex))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = node.name,
                            color = VaultDesignTokens.SanctuaryParchment,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${node.type.displayName} • ${node.region.displayName}",
                            color = VaultDesignTokens.DimParchment,
                            fontSize = 11.sp
                        )
                        if (node.bonusDescription.isNotBlank()) {
                            Text(
                                text = "Bonus: ${node.bonusDescription}",
                                color = VaultDesignTokens.RunicTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onToggleNodeCompletion(node) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCompleted) VaultDesignTokens.CharcoalBorder else VaultDesignTokens.EmberRed
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.Check else Icons.Default.Check,
                                contentDescription = "Toggle",
                                modifier = Modifier.size(14.dp),
                                tint = if (isCompleted) VaultDesignTokens.RunicTeal else Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isCompleted) "Completed" else "Mark Done",
                                fontSize = 11.sp
                            )
                        }

                        IconButton(
                            onClick = { selectedNode = null },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = VaultDesignTokens.DimParchment,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
