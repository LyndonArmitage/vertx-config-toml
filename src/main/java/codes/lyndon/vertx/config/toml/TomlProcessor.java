package codes.lyndon.vertx.config.toml;

import com.moandjiezana.toml.Toml;
import io.vertx.config.spi.ConfigProcessor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * <p>
 * A processor for the <a href='https://github.com/toml-lang/toml'>TOML</a>
 * format.
 * </p>
 * <p>
 * Currently using the
 * <a href='https://github.com/mwanji/toml4j'>toml4j library</a>. If a newer
 * version of this library becomes available, you can override the dependency
 * (assuming the API does not change).
 * </p>
 *
 * @author <a href='lyndon.codes'>Lyndon Armitage</a>
 */
public final class TomlProcessor implements ConfigProcessor {

  @NotNull
  @Contract(pure = true)
  @Override
  public final String name() {
    return "toml";
  }

  @Override
  public void process(
          @NotNull Vertx vertx,
          @Nullable JsonObject configuration,
          @NotNull Buffer input,
          @NotNull Handler<AsyncResult<JsonObject>> handler
  ) {
    if (input.length() == 0) {
      handler.handle(Future.succeededFuture(new JsonObject()));
      return;
    }

    vertx.executeBlocking(
            future -> {
              try {
                // Currently loads the whole config file into memory
                ByteArrayInputStream inputStream = new ByteArrayInputStream(
                        input.getBytes()
                );
                Toml toml = new Toml().read(inputStream);
                Map<String, Object> asMap = toml.toMap();
                JsonObject asJson = new JsonObject(asMap);
                future.complete(asJson);
              } catch (Exception e) {
                future.fail(e);
              }
            },
            handler
    );
  }
}
