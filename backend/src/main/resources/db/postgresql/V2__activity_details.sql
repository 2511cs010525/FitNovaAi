alter table workouts add column energy_after integer;
alter table workouts add column notes varchar(500);
alter table form_sessions add column best_score integer;
alter table form_sessions add column duration_seconds integer;
create index idx_meal_user_created on meals(user_id, created_at);
create index idx_form_user_created on form_sessions(user_id, created_at);
