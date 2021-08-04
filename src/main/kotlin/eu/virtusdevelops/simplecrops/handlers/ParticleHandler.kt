package eu.virtusdevelops.simplecrops.handlers

import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI
import com.github.fierioziy.particlenativeapi.api.Particles_1_13
import com.github.fierioziy.particlenativeapi.api.Particles_1_8
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore
import eu.virtusdevelops.simplecrops.SimpleCrops
import eu.virtusdevelops.simplecrops.util.VectorUtils
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.random.Random
import org.bukkit.Color as Color1

class ParticleHandler(plugin: SimpleCrops) {
    private var particles_1_8: Particles_1_8
    private var particles_1_13: Particles_1_13

    init {
        val api: ParticleNativeAPI = ParticleNativeCore.loadAPI(plugin)
        particles_1_8 = api.particles_1_8
        particles_1_13 = api.particles_1_13
    }




    fun playBreakParticles(player: Player, location: Location){
        location.add(0.5, 0.3, 0.5)
        var vector = Vector(0.1, 0.0, 0.1)



        val packetDustColorTransition: Any = particles_1_13.DUST_COLOR_TRANSITION()
            .color(
                org.bukkit.Color.fromBGR(0, 0, 255),
                org.bukkit.Color.fromBGR(0,0,0),
                2.0)
            .packet(true, location)
        particles_1_13.sendPacket(player, packetDustColorTransition)

//        for (i in 0 until 50) {
//            vector = VectorUtils.rotateAroundAxisY(vector, 360.0/50.0)
//            val packet = particles_1_8.FLAME().packetMotion(true,
//                location,
//                vector.add(Vector(
//                    -0.1 + Random.nextDouble() * 0.1,
//                    -0.1 + Random.nextDouble() * 0.1,
//                    -0.1 + Random.nextDouble() * 0.1)
//                ))
//            particles_1_8.sendPacket(player, packet)
//        }
    }



}