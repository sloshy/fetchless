package fetchless

import cats.{FlatMap, Functor, Traverse}
import cats.syntax.all._
import cats.Monad
import cats.data.Kleisli

object syntax {

  implicit class FDedupedFetchSyntax[F[_]: FlatMap, A](fdf: F[DedupedFetch[F, A]]) {
    def alsoFetch[I, B](i: I)(implicit fetch: Fetch[F, I, B]) = fdf.flatMap { df =>
      df.alsoFetch(i)
    }
    def alsoFetch[I, B](fetch: Fetch[F, I, B])(i: I) = fdf.flatMap { df =>
      df.alsoFetch(fetch)(i)
    }
    def alsoFetchAll[I, B](is: Set[I])(implicit fetch: Fetch[F, I, B]) = fdf.flatMap { df =>
      df.alsoFetchAll(is)
    }
    def alsoFetchAll[I, B](fetch: Fetch[F, I, B])(is: Set[I]) = fdf.flatMap { df =>
      df.alsoFetchAll(fetch)(is)
    }
  }

  implicit class SingleSyntax[I](i: I) {

    /**
     * Fetches a single result. Does not try to auto-batch requests.
     */
    def fetch[F[_], A](implicit fetch: Fetch[F, I, A]): F[Option[A]] = fetch.single(i)
    def fetchLazy[F[_], A](implicit fetch: Fetch[F, I, A]): LazyFetch[F, Option[A]] =
      fetch.singleLazy(i)
  }

  implicit class EffectfulSyntax[F[_]: FlatMap, I](fi: F[I]) {

    /**
     * Fetches a single result. Does not try to auto-batch requests.
     */
    def fetch[A](implicit fetch: Fetch[F, I, A]): F[Option[A]] = fi.flatMap(fetch.single(_))
    def fetchLazy[A](implicit fetch: Fetch[F, I, A], M: Monad[F]) =
      LazyFetch.liftF(fi).flatMap(i => fetch.singleLazy(i))
  }

  implicit class TraverseBatchSyntax[G[_]: Traverse, I](is: G[I]) {

    /**
     * Fetches all results in the current collection. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is)
  }

  implicit class Tuple2BatchSyntax[I](is: (I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    def fetchAllLazy[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batchLazy(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2)).map { m =>
        (m.get(is._1), m.get(is._2))
      }
    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2)).map { m =>
        (m.get(is._1), m.get(is._2))
      }
    def fetchTupledLazy[F[_]: Monad, A](fetch: Fetch[F, I, A]) =
      fetch.batchLazy(Set(is._1, is._2)).map { m =>
        (m.get(is._1), m.get(is._2))
      }
  }

  implicit class Tuple3BatchSyntax[I](is: (I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3)).map { m =>
        (m.get(is._1), m.get(is._2), m.get(is._3))
      }
    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3)).map { m =>
        (m.get(is._1), m.get(is._2), m.get(is._3))
      }
    def fetchTupled[F[_]: Monad, A](fetch: Fetch[F, I, A]) =
      fetch.batchLazy(Set(is._1, is._2, is._3)).map { m =>
        (m.get(is._1), m.get(is._2), m.get(is._3))
      }
  }

  implicit class Tuple4BatchSyntax[I](is: (I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4)).map { m =>
        (m.get(is._1), m.get(is._2), m.get(is._3), m.get(is._4))
      }
    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4)).map { m =>
        (m.get(is._1), m.get(is._2), m.get(is._3), m.get(is._4))
      }
  }

  implicit class Tuple5BatchSyntax[I](is: (I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4, is._5)).map { m =>
        (m.get(is._1), m.get(is._2), m.get(is._3), m.get(is._4), m.get(is._5))
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4, is._5)).map { m =>
        (m.get(is._1), m.get(is._2), m.get(is._3), m.get(is._4), m.get(is._5))
      }
  }

  implicit class Tuple6BatchSyntax[I](is: (I, I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4, is._5, is._6)).map { m =>
        (m.get(is._1), m.get(is._2), m.get(is._3), m.get(is._4), m.get(is._5), m.get(is._6))
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4, is._5, is._6)).map { m =>
        (m.get(is._1), m.get(is._2), m.get(is._3), m.get(is._4), m.get(is._5), m.get(is._6))
      }
  }

  implicit class Tuple7BatchSyntax[I](is: (I, I, I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4, is._5, is._6, is._7)).map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4, is._5, is._6, is._7)).map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7)
        )
      }
  }

  implicit class Tuple8BatchSyntax[I](is: (I, I, I, I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4, is._5, is._6, is._7, is._8)).map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4, is._5, is._6, is._7, is._8)).map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8)
        )
      }
  }

  implicit class Tuple9BatchSyntax[I](is: (I, I, I, I, I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4, is._5, is._6, is._7, is._8, is._9)).map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4, is._5, is._6, is._7, is._8, is._9)).map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9)
        )
      }
  }

  implicit class Tuple10BatchSyntax[I](is: (I, I, I, I, I, I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4, is._5, is._6, is._7, is._8, is._9, is._10)).map {
        m =>
          (
            m.get(is._1),
            m.get(is._2),
            m.get(is._3),
            m.get(is._4),
            m.get(is._5),
            m.get(is._6),
            m.get(is._7),
            m.get(is._8),
            m.get(is._9),
            m.get(is._10)
          )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) =
      fetch.batch(Set(is._1, is._2, is._3, is._4, is._5, is._6, is._7, is._8, is._9, is._10)).map {
        m =>
          (
            m.get(is._1),
            m.get(is._2),
            m.get(is._3),
            m.get(is._4),
            m.get(is._5),
            m.get(is._6),
            m.get(is._7),
            m.get(is._8),
            m.get(is._9),
            m.get(is._10)
          )
      }
  }

  implicit class Tuple11BatchSyntax[I](is: (I, I, I, I, I, I, I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) = fetch
      .batch(Set(is._1, is._2, is._3, is._4, is._5, is._6, is._7, is._8, is._9, is._10, is._11))
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) = fetch
      .batch(Set(is._1, is._2, is._3, is._4, is._5, is._6, is._7, is._8, is._9, is._10, is._11))
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11)
        )
      }
  }

  implicit class Tuple12BatchSyntax[I](is: (I, I, I, I, I, I, I, I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(is._1, is._2, is._3, is._4, is._5, is._6, is._7, is._8, is._9, is._10, is._11, is._12)
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(is._1, is._2, is._3, is._4, is._5, is._6, is._7, is._8, is._9, is._10, is._11, is._12)
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12)
        )
      }
  }

  implicit class Tuple13BatchSyntax[I](is: (I, I, I, I, I, I, I, I, I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13)
        )
      }
  }

  implicit class Tuple14BatchSyntax[I](is: (I, I, I, I, I, I, I, I, I, I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14)
        )
      }
  }

  implicit class Tuple15BatchSyntax[I](is: (I, I, I, I, I, I, I, I, I, I, I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15)
        )
      }
  }

  implicit class Tuple16BatchSyntax[I](is: (I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16)
        )
      }
  }

  implicit class Tuple17BatchSyntax[I](is: (I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16,
          is._17
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16),
          m.get(is._17)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16,
          is._17
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16),
          m.get(is._17)
        )
      }
  }

  implicit class Tuple18BatchSyntax[I](is: (I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I)) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16,
          is._17,
          is._18
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16),
          m.get(is._17),
          m.get(is._18)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16,
          is._17,
          is._18
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16),
          m.get(is._17),
          m.get(is._18)
        )
      }
  }

  implicit class Tuple19BatchSyntax[I](
      is: (I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I)
  ) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16,
          is._17,
          is._18,
          is._19
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16),
          m.get(is._17),
          m.get(is._18),
          m.get(is._19)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16,
          is._17,
          is._18,
          is._19
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16),
          m.get(is._17),
          m.get(is._18),
          m.get(is._19)
        )
      }
  }

  implicit class Tuple20BatchSyntax[I](
      is: (I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I)
  ) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16,
          is._17,
          is._18,
          is._19,
          is._20
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16),
          m.get(is._17),
          m.get(is._18),
          m.get(is._19),
          m.get(is._20)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16,
          is._17,
          is._18,
          is._19,
          is._20
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16),
          m.get(is._17),
          m.get(is._18),
          m.get(is._19),
          m.get(is._20)
        )
      }
  }

  implicit class Tuple21BatchSyntax[I](
      is: (I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I)
  ) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16,
          is._17,
          is._18,
          is._19,
          is._20,
          is._21
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16),
          m.get(is._17),
          m.get(is._18),
          m.get(is._19),
          m.get(is._20),
          m.get(is._21)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16,
          is._17,
          is._18,
          is._19,
          is._20,
          is._21
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16),
          m.get(is._17),
          m.get(is._18),
          m.get(is._19),
          m.get(is._20),
          m.get(is._21)
        )
      }
  }

  implicit class Tuple22BatchSyntax[I](
      is: (I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I, I)
  ) {

    /**
     * Fetches all results in the current tuple, as a map. Will try to batch requests if your Fetch
     * instance supports it.
     */
    def fetchAll[F[_], A](implicit fetch: Fetch[F, I, A]) =
      fetch.batch(is.productIterator.asInstanceOf[Iterator[I]].toSet)

    /**
     * Fetches all results in the current tuple, retaining the tuple structure. Will try to batch
     * requests if your `Fetch` instance supports it.
     */
    def fetchTupled[F[_]: Functor, A](implicit fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16,
          is._17,
          is._18,
          is._19,
          is._20,
          is._21,
          is._22
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16),
          m.get(is._17),
          m.get(is._18),
          m.get(is._19),
          m.get(is._20),
          m.get(is._21),
          m.get(is._22)
        )
      }

    def fetchTupled[F[_]: Functor, A](fetch: Fetch[F, I, A]) = fetch
      .batch(
        Set(
          is._1,
          is._2,
          is._3,
          is._4,
          is._5,
          is._6,
          is._7,
          is._8,
          is._9,
          is._10,
          is._11,
          is._12,
          is._13,
          is._14,
          is._15,
          is._16,
          is._17,
          is._18,
          is._19,
          is._20,
          is._21,
          is._22
        )
      )
      .map { m =>
        (
          m.get(is._1),
          m.get(is._2),
          m.get(is._3),
          m.get(is._4),
          m.get(is._5),
          m.get(is._6),
          m.get(is._7),
          m.get(is._8),
          m.get(is._9),
          m.get(is._10),
          m.get(is._11),
          m.get(is._12),
          m.get(is._13),
          m.get(is._14),
          m.get(is._15),
          m.get(is._16),
          m.get(is._17),
          m.get(is._18),
          m.get(is._19),
          m.get(is._20),
          m.get(is._21),
          m.get(is._22)
        )
      }
  }
}
