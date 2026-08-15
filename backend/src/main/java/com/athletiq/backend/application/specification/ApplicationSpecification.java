package com.athletiq.backend.application.specification;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.application.entity.ApplicationStatus;
import org.springframework.data.jpa.domain.Specification;

public final class ApplicationSpecification {

    private ApplicationSpecification() {
    }

    public static Specification<Application> eventId(
            Long eventId
    ) {

        return (
                root,
                query,
                cb
        ) ->
                cb.equal(
                        root.get("event").get("id"),
                        eventId
                );
    }

    public static Specification<Application> search(
            String search
    ) {

        return (
                root,
                query,
                cb
        ) -> {

            String pattern =
                    "%" +
                    search.trim().toLowerCase() +
                    "%";

            return cb.or(
                    cb.like(
                            cb.lower(
                                    cb.coalesce(
                                            root.get("applicantName"),
                                            ""
                                    )
                            ),
                            pattern
                    ),
                    cb.like(
                            cb.lower(
                                    cb.coalesce(
                                            root.get("applicantEmail"),
                                            ""
                                    )
                            ),
                            pattern
                    ),
                    cb.like(
                            cb.coalesce(
                                    root.get("applicantPhone"),
                                    ""
                            ),
                            pattern
                    )
            );
        };
    }

    public static Specification<Application> email(
            String email
    ) {

        return (
                root,
                query,
                cb
        ) ->
                cb.like(
                        cb.lower(
                                cb.coalesce(
                                        root.get("applicantEmail"),
                                        ""
                                )
                        ),
                        "%" +
                        email.trim().toLowerCase() +
                        "%"
                );
    }

    public static Specification<Application> position(
            String position
    ) {

        String pattern =
                "%\"position\":\"" +
                position.trim().toLowerCase() +
                "%";

        return (
                root,
                query,
                cb
        ) ->
                cb.like(
                        cb.lower(
                                cb.coalesce(
                                        root.get("submittedData"),
                                        ""
                                )
                        ),
                        pattern
                );
    }

    public static Specification<Application> age(
            Integer age
    ) {

        String pattern =
                "%\"age\":" +
                age +
                "%";

        return (
                root,
                query,
                cb
        ) ->
                cb.like(
                        cb.coalesce(
                                root.get("submittedData"),
                                ""
                        ),
                        pattern
                );
    }

    public static Specification<Application> status(
            ApplicationStatus status
    ) {

        return (
                root,
                query,
                cb
        ) ->
                cb.equal(
                        root.get("status"),
                        status
                );
    }
}