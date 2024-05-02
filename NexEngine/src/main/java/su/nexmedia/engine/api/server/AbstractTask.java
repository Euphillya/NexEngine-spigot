package su.nexmedia.engine.api.server;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.NexPlugin;

import java.util.concurrent.TimeUnit;

public abstract class AbstractTask<P extends NexPlugin<P>> {

    @NotNull protected final P plugin;

    protected ScheduledTask taskId;
    protected long    interval;
    protected boolean async;

    public AbstractTask(@NotNull P plugin, int interval, boolean async) {
        this(plugin, interval * 20L, async);
    }

    public AbstractTask(@NotNull P plugin, long interval, boolean async) {
        this.plugin = plugin;
        this.interval = interval;
        this.async = async;
        this.taskId = null;
    }

    public abstract void action();

    public final void restart() {
        this.stop();
        this.start();
    }

    public boolean start() {
        if (this.taskId != null) return false;
        if (this.interval <= 0L) return false;

        if (this.async) {
            this.taskId = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task -> action(), 1L, interval*50, TimeUnit.MILLISECONDS);
        }
        else {
            this.taskId = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, task -> action(), 1L, interval);
        }
        return this.taskId.getExecutionState().equals(ScheduledTask.ExecutionState.RUNNING);
    }

    public boolean stop() {
        if (this.taskId == null) return false;

        this.taskId.cancel();
        this.taskId = null;
        return true;
    }
}
