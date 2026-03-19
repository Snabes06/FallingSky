package sw.surasnipers.fallingsky.client


import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType
import net.minecraft.entity.Entity
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.command.CommandManager
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.hit.HitResult
import net.minecraft.world.RaycastContext
import org.lwjgl.glfw.GLFW
import kotlin.math.acos

class FallingskyClient : ClientModInitializer {

    //glow enable
    companion object {
        @JvmStatic
        var glowEnabled = false

        @JvmStatic
        fun isGlowEnabled(): Boolean {
            return glowEnabled
        }


        //glow color
        @JvmStatic
        var currentColor: Int = 0xFF00FF00.toInt() // default green

        @JvmStatic
        fun setColor(name: String) {
            currentColor = when (name.lowercase()) {
                "green" -> 0xFF00FF00.toInt()
                "yellow" -> 0xFFFFFF00.toInt()
                "red" -> 0xFFFF0000.toInt()
                else -> currentColor
            }
        }

        @JvmStatic
        fun getGlowColor(): Int {
            return currentColor
        }

        //glow entity
        @JvmStatic
        var entity = "minecraft:glow_squid"

        @JvmStatic
        fun getGlowEntity(): String {
            return entity
        }

        @JvmStatic
        fun isCorrectEntity(entity: Entity): Boolean {
            val id = if (this.entity.contains(":")) {
                Identifier.tryParse(this.entity)
            } else {
                Identifier.of("minecraft", this.entity)
            } ?: return false

            val selectedType = Registries.ENTITY_TYPE.get(id)

            return entity.type == selectedType
        }

        //line of site checker
        @JvmStatic
        fun isEntityInLineOfSight(entity: Entity): Boolean {
            val client = MinecraftClient.getInstance()
            val player = client.player ?: return false
            val world = client.world ?: return false
            val playerPos = player.getCameraPosVec(1.0f)
            val entityPos = entity.entityPos
            val lookVec = player.getRotationVec(1.0f)
            val toEntity = entityPos.subtract(playerPos).normalize()
            val dot = lookVec.dotProduct(toEntity)
            val angle = acos(dot)
            val maxAngle = Math.toRadians(client.options.fov.value.toDouble()/1.5)
            if (!(angle < maxAngle)) return false

            // Check for blocks in the way
            val raycastContext = RaycastContext(player.getCameraPosVec(1.0f), entityPos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player)
            val hitResult = world.raycast(raycastContext)
            if (hitResult.type == HitResult.Type.BLOCK) {
                val hitPos = hitResult.pos
                val distToHit = playerPos.distanceTo(hitPos)
                val distToEntity = playerPos.distanceTo(entityPos)
                if (distToHit < distToEntity - 1.0) {
                    return false
                }
            }
            return true
        }
    }

    private lateinit var toggleKey: KeyBinding

    override fun onInitializeClient() {

        toggleKey = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.fallingsky.toggle_glow",
                GLFW.GLFW_KEY_G,
                KeyBinding.Category.MISC
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client: MinecraftClient ->

            while (toggleKey.wasPressed()) {
                glowEnabled = !glowEnabled
                println("Glow toggled: $glowEnabled")
            }
        }

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                CommandManager.literal("glow")
                    .then(CommandManager.argument("value", BoolArgumentType.bool()).executes { context ->
                        val newGlow = BoolArgumentType.getBool(context, "value")
                        glowEnabled = newGlow
                        MinecraftClient.getInstance().player?.sendMessage(Text.of("Glow set to whit varibule: $glowEnabled"), false)
                        1
                    })
                    .executes{context ->
                        glowEnabled = !glowEnabled
                        MinecraftClient.getInstance().player?.sendMessage(Text.of("Glow toggled: $glowEnabled"), false)
                        1
                    }
            )

        }


        CommandRegistrationCallback.EVENT.register { dispatcher, _, _->
            dispatcher.register(
                CommandManager.literal("glowC")
                    .then(
                        CommandManager.argument("color", StringArgumentType.word()).suggests { _, builder ->
                            listOf("red", "green", "yellow").forEach { builder.suggest(it) }
                            builder.buildFuture()
                        }
                            .executes { context ->

                                val input = StringArgumentType.getString(context, "color").lowercase()

                                val color = when (input) {
                                    "green" -> Formatting.GREEN
                                    "yellow" -> Formatting.YELLOW
                                    "red" -> Formatting.RED
                                    else -> null
                                }

                                if (color == null) {
                                    MinecraftClient.getInstance().player?.sendMessage(
                                        Text.of("Invalid color! Use: red, green, yellow"),
                                        false
                                    )
                                    return@executes 0
                                }

                                // Save selected color
                                setColor(input)

                                MinecraftClient.getInstance().player?.sendMessage(
                                    Text.literal("Glow color set to $input").formatted(color),
                                    false
                                )

                                1
                            }
                    )
            )

        }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommandManager.literal("glowE")
                    .then(
                        ClientCommandManager.argument("entity", StringArgumentType.greedyString())
                            .suggests { _, builder ->
                                Registries.ENTITY_TYPE.ids.forEach {
                                    builder.suggest(it.toString())
                                }
                                builder.buildFuture()
                            }
                            .executes { context ->
                                val input = StringArgumentType.getString(context, "entity")
                                val id = if (input.contains(":")) {
                                    Identifier.tryParse(input)
                                } else {
                                    Identifier.of("minecraft", input)
                                }

                                if (id == null || !Registries.ENTITY_TYPE.containsId(id)) {
                                    MinecraftClient.getInstance().player?.sendMessage(
                                        Text.of("Invalid entity type: $input"),
                                        false
                                    )
                                    return@executes 0
                                }

                                entity = id.toString()
                                1
                            }
                    )
            )
        }



    }
}