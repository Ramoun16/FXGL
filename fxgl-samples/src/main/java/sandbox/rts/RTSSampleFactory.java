/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package sandbox.rts;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class RTSSampleFactory implements EntityFactory {

    @Spawns("TownHall")
    public Entity newTownHall(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(RTSSampleType.TOWN_HALL)
                .viewFromNodeWithBBox(new Circle(40, Color.RED))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("Worker")
    public Entity newWorker(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(RTSSampleType.WORKER)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.color(FXGLMath.random(), FXGLMath.random(), FXGLMath.random())))
                .with(new CollidableComponent(true), new BackpackComponent())
                .with(new FSMControl<>(WorkerState.class, WorkerState.IDLE))
                .build();
    }

    @Spawns("GoldMine")
    public Entity newGoldMine(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(RTSSampleType.GOLD_MINE)
                .viewFromNodeWithBBox(new Circle(20, Color.GOLD))
                .with(new CollidableComponent(true), new GoldMineComponent())
                .build();
    }
}
