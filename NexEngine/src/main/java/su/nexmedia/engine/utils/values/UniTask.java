package su.nexmedia.engine.utils.values;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.NexPlugin;

import java.util.concurrent.TimeUnit;

public class UniTask {

    private final NexPlugin<?> plugin;
    private final Runnable runnable;
    private final long    interval;
    private final boolean async;

    private ScheduledTask taskId;

    public UniTask(@NotNull NexPlugin<?> plugin, @NotNull Runnable runnable, long interval, boolean async) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.interval = interval;
        this.async = async;

        this.taskId = null;
    }

    @NotNull
    public static Builder builder(@NotNull NexPlugin<?> plugin) {
        return new Builder(plugin);
    }

    public final void restart() {
        this.stop();
        this.start();
    }

    public boolean start() {
        if (this.taskId != null) return false;
        if (this.interval <= 0L) return false;

        if (this.async) {
            this.taskId = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> runnable.run(), 1L, interval*50, TimeUnit.MILLISECONDS);
        }
        else {
            this.taskId = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, task -> runnable.run(), 1L, interval);
        }
        return true;
    }

    public boolean stop() {
        if (this.taskId == null) return false;

        this.taskId.cancel();
        this.taskId = null;
        return true;
    }

    public static class Builder {

        private final NexPlugin<?> plugin;

        private Runnable runnable;
        private long     interval;
        private boolean  async;

        public Builder(@NotNull NexPlugin<?> plugin) {
            this.plugin = plugin;
        }

        @NotNull
        public Builder withRunnable(@NotNull Runnable runnable) {
            this.runnable = runnable;
            return this;
        }

        @NotNull
        public Builder withTicks(long interval) {
            this.interval = interval;
            return this;
        }

        @NotNull
        public Builder withSeconds(int interval) {
            this.interval = interval * 20L;
            return this;
        }

        @NotNull
        public Builder async() {
            this.async = true;
            return this;
        }

        @NotNull
        public UniTask build() {
            return new UniTask(plugin, runnable, interval, async);
        }

        @NotNull
        public UniTask buildAndRun() {
            UniTask task = this.build();
            task.start();
            return task;
        }
    }
}
