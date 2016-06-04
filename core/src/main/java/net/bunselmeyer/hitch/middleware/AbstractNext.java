package net.bunselmeyer.hitch.middleware;

import java.util.Iterator;

public abstract class AbstractNext<Q, P> implements Next {

    private final Iterator<Middleware<Q, P>> stack;
    private final Q req;
    private final P res;

    private Object memo;

    protected AbstractNext(Iterator<Middleware<Q, P>> stack, Q req, P res) {
        this.stack = stack;
        this.req = req;
        this.res = res;
    }

    protected abstract void handleException(Throwable err);

    protected abstract void handleNotFound();

    protected abstract boolean isCommitted();

    @Override
    public <T> T memo() {
        return (T) memo;
    }

    @Override
    public void visit(Middleware middleware, Object memo) {
        this.run(memo);
    }

    @Override
    public void visit(Middleware.StandardMiddleware1 middleware, Object memo) {
        try {
            middleware.run(req, res);
            this.run(memo); // todo: should we automatically call the next middleware?
        } catch (Throwable e) {
            this.run(e);
        }
    }

    @Override
    public void visit(Middleware.StandardMiddleware2 middleware, Object memo) {
        if (memo instanceof Throwable) {
            this.run(memo);
            return;
        }
        try {
            Object newMemo = middleware.run(req, res);
            if (newMemo == null && memo != null) {
                newMemo = memo;
            }
            this.run(newMemo);
        } catch (Throwable e) {
            this.run(e);
        }
    }

    @Override
    public void visit(Middleware.StandardMiddleware3 middleware, Object memo) {
        if (memo instanceof Throwable) {
            this.run(memo);
            return;
        }

        try {
            Object newMemo = middleware.run(memo, req, res);
            if (newMemo == null && memo != null) {
                newMemo = memo;
            }
            this.run(newMemo);
        } catch (Throwable e) {
            this.run(e);
        }
    }

    @Override
    public void visit(Middleware.StandardMiddleware4 middleware, Object memo) {
        try {
            middleware.run(req, res, this);
        } catch (Throwable e) {
            this.run(e);
        }
    }

    @Override
    public void visit(Middleware.StandardMiddleware5 middleware, Object memo) {
        if (memo instanceof Throwable) {
            this.run(memo);
            return;
        }
        try {
            middleware.run(memo, req, res, this);
        } catch (Throwable e) {
            this.run(e);
        }
    }

    @Override
    public void visit(Middleware.ExceptionMiddleware middleware, Object memo) {
        Exception m = null;
        try {
            m = (Exception) memo;
        } catch (ClassCastException e) {
            this.run(memo);
            return;
        }
        try {
            middleware.run(m, req, res, this);
        } catch (Throwable e) {
            this.run(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, U, E extends Throwable> void visit(Middleware.CheckedExceptionMiddleware<T, U, E> middleware, Object memo) {
        E m = null;
        try {
            m = (E) memo;
        } catch (ClassCastException e) {
            this.run(m);
            return;
        }
        try {
            middleware.run(m, (T) req, (U) res, this);
        } catch (Throwable e) {
            this.run(e);
        }
    }

    @Override
    public final void run(Object memo) {

        this.memo = memo;

        if (isCommitted()) {
            return;
        }

        if (!stack.hasNext()) {
            if (memo instanceof Throwable) {
                handleException((Throwable) memo);

            } else {
                handleNotFound();
            }
        }

        Middleware<Q, P> middleware = stack.next();

        middleware.accept(this, memo);

        //App.runner(middleware, err, req, res, this);
    }
}
