package eu.virtusdevelops.simplecrops.handlers


import eu.virtusdevelops.simplecrops.SimpleCrops
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import kotlin.random.Random


class ParticleHandler(plugin: SimpleCrops) {
//    private var particles_1_8: Particles_1_8
//    private var particles_1_13: Particles_1_13

    init {
//        val api: ParticleNativeAPI = ParticleNativeCore.loadAPI(plugin)
//        particles_1_8 = api.particles_1_8
//        particles_1_13 = api.particles_1_13
    }



    fun playBoneMealParticle(player: Player, location: Location){
        location.add(0.0, 0.3, 0.0)
        for(i in 0 until 10){

            player.spawnParticle(Particle.FLAME,
                location.clone().add(
                    -0.1 + Random.nextDouble() * 1.0,
                    -0.2 + Random.nextDouble() * 0.4,
                    -0.1 + Random.nextDouble() * 1.0
                )
            , 0)

//            val particle2 = particles_1_13.FLAME().packet(true,
//                    location.clone().add(
//                        -0.1 + Random.nextDouble() * 1.0,
//                        -0.2 + Random.nextDouble() * 0.4,
//                        -0.1 + Random.nextDouble() * 1.0
//                    )
//                )
//            particles_1_13.sendPacket(player, particle2)
        }
    }

    fun growEffect(player: Player, location: Location){
        playBoneMealParticle(player, location)
        /*location.add(0.5, 0.3, 0.5)
        var stepY = -60.0
        while (stepY < 60) {
            val dx: Double = -kotlin.math.cos((0 + stepY) / 90.0 * kotlin.math.PI * 2) * 0.6
            val dy: Double = stepY / 30.0 / 2.0
            val dz: Double = -kotlin.math.sin((0 + stepY) / 90.0 * kotlin.math.PI * 2) * 0.6


            val packet = particles_1_13.FLAME().packet(false, location.clone().add(dx, dy, dz))
            particles_1_13.sendPacket(player, packet)

            stepY += 120.0 / 50
        }*/
    }


    fun playBreakParticles(player: Player, location: Location){
//        location.add(0.5, 0.3, 0.5)
//        var vector = Vector(0.1, 0.0, 0.1)


        player.spawnParticle(Particle.FLAME, location, 0)


//        val packetDustColorTransition: Any = particles_1_13.DUST_COLOR_TRANSITION()
//            .color(
//                org.bukkit.Color.fromBGR(0, 0, 255),
//                org.bukkit.Color.fromBGR(0,0,0),
//                2.0)
//            .packet(false, location)
//        particles_1_13.sendPacket(player, packetDustColorTransition)

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