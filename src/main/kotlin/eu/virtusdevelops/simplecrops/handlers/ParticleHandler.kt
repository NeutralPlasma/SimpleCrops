package eu.virtusdevelops.simplecrops.handlers


import eu.virtusdevelops.simplecrops.SimpleCrops
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import kotlin.random.Random


class ParticleHandler(plugin: SimpleCrops) {

    init {
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
        }
    }

    fun growEffect(player: Player, location: Location){

        location.add(0.0, 0.3, 0.0)
        for(i in 0 until 10){

            player.spawnParticle(Particle.FALLING_HONEY,
                location.clone().add(
                    -0.1 + Random.nextDouble() * 1.0,
                    -0.2 + Random.nextDouble() * 0.4,
                    -0.1 + Random.nextDouble() * 1.0
                )
                , 0)
        }

    }


    fun playBreakParticles(player: Player, location: Location){
//        location.add(0.5, 0.3, 0.5)
//        var vector = Vector(0.1, 0.0, 0.1)


        location.add(0.0, 0.3, 0.0)
        for(i in 0 until 20){

            player.spawnParticle(Particle.ASH,
                location.clone().add(
                    -0.1 + Random.nextDouble() * 1.0,
                    -0.2 + Random.nextDouble() * 0.4,
                    -0.1 + Random.nextDouble() * 1.0
                )
                , 0)
        }

    }



}