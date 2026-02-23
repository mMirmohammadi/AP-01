package util;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

import page.Message;

/**
 * Effect is an immutable class that represent a work
 * in world that make a result
 * @param <T> type of result
 */
public class Effect<T> {

  public interface EffectCallback<U> {
    public void success(U result);
    public void failure(Throwable error);
  }

  public interface EffectContainer<U> {
    public void execute(EffectCallback<U> handler);
  }

  public interface Function<V,U> {
    public U apply(V arg) throws Throwable;
  }

  public interface Task {
    void run() throws Throwable;
  }

  public static final Effect<Unit> noOp = new Effect<>(h->h.success(Unit.value));

  private final EffectContainer<T> data;

  public Effect(EffectContainer<T> data) {
    this.data = data;
  }

  public void execute() {
    data.execute(new EffectCallback<>() {

      @Override
      public void success(T result) {}

      @Override
      public void failure(Throwable error) {}

    });
  }

  /**
   * @param <U> type of value
   * @param value 
   * @return a Effect wrapper that contain value
   */
  public static <U> Effect<U> ok(U value) {
    return new Effect<>((h) -> h.success(value));
  }

  public static Effect<Unit> ok() {
    return Effect.ok(Unit.value);
  }

  /**
   * @param <U>
   * @param error the message of error
   * @return a Effect value failed with error
   */
  public static <U> Effect<U> error(Throwable error) {
    return new Effect<>((h) -> h.failure(error));
  }

  /**
   * chain Effects and fails with first error
   * @param <U>
   * @param mapper a function that transform this value to another Effect
   * @return combined Effect
   */
  public <U> Effect<U> flatMap(Function<T, Effect<U>> mapper) {
    return new Effect<U>((h)->{
      data.execute(new EffectCallback<T>() {

        @Override
        public void success(T result) {
          try{
            mapper.apply(result).data.execute(h);
          }
          catch(Throwable e) {
            h.failure(e);
          }
        }

        @Override
        public void failure(Throwable error) {
          h.failure(error);
        }
        
      });
    });
  }

  /**
   * transform inside data with a function in case of success
   * @param <U>
   * @param mapper a function that transform this value to another value
   * @return transformed Effect
   */
  public <U> Effect<U> map(Function<T, U> mapper) {
    return new Effect<U>((h)->{
      data.execute(new EffectCallback<T>() {

        @Override
        public void success(T result) {
          try{
            h.success(mapper.apply(result));
          }
          catch(Throwable e) {
            h.failure(e);
          }
        }

        @Override
        public void failure(Throwable error) {
          h.failure(error);
        }
      
      });
    });
  }

  public Effect<Unit> discardData() {
    return this.map(x -> Unit.value);
  }

  public Effect<Unit> consume(Consumer<T> consumer) {
    return this.map(x -> {
      consumer.accept(x);
      return Unit.value;
    });
  }

  /**
   * show contained value in a {@link Message} page in case of success
   * @return Effect of operation
   */
  public Effect<Unit> show() {
    return this.flatMap(x -> new Message(x.toString()).action());
  }

  /**
   * show error in a {@link Message} page in case of error
   * @return this
   */
  public Effect<Unit> showError() {
    //if (isError()) new Message("Error: " + getError()).action();
    return discardData()
      .catchThen(e -> new Message("Error: " + e.getMessage()).action());
  }

  /**
   * lift a supplier with Effect monad
   * @param <U>
   * @param supplier
   * @return lifted supplier
   */
  public static <U> Supplier<Effect<U>> liftSupplier(Supplier<U> supplier) {
    return () -> Effect.ok(supplier.get());
  }

  public void evaluate(EffectCallback<T> h) {
    data.execute(h);
  }

  public Effect<T> catchThen(Function<Throwable, Effect<T>> f) {
    return new Effect<T>((h)->{
      data.execute(new EffectCallback<T>() {

        @Override
        public void success(T result) {
          h.success(result);
        }

        @Override
        public void failure(Throwable error) {
          try{
            f.apply(error).evaluate(h);
          }
          catch(Throwable e) {
            h.failure(e);
          }
        }
        
      });
    });
  }

  public static Effect<Unit> syncWork(Task task) {
    return new Effect<>(h -> {
      try{
        task.run();
        h.success(Unit.value);  
      }
      catch(Throwable e) {
        h.failure(e);
      }
    });
  }

  public static <U> Effect<U> error(String string) {
    return Effect.error(new Error(string));
  }

  public <U> Effect<U> then(Effect<U> w) {
    return this.flatMap(r -> w);
  }

  public static <X> Effect<X> fromSync(Supplier<X> x) {
    return new Effect<>(h -> {
      try{
        h.success(x.get());  
      }
      catch(Throwable e) {
        h.failure(e);
      }
    });
  }
}