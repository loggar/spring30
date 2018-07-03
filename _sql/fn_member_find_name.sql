DELIMITER $$

drop function if exists springbook.fn_member_find_name$$
create function springbook.fn_member_find_name(in_id INT) returns varchar(255)
begin
	declare out_name varchar(255);
		select name into out_name
		from member
		where id = in_id;
	return out_name;
end $$

DELIMITER ;