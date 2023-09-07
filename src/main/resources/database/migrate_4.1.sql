/* Remove duplicate recipients */


-- First, link to_recipient to the oldest corresponding recipient
UPDATE to_recipient tr
SET rcp_id = (
    select min(r2.rcp_id)
	from recipient r1, recipient r2
	where r1.RCP_LOGIN is null 
	and r2.RCP_LOGIN is null
	and tr.RCP_ID = r1.RCP_ID
	and r1.RCP_PHONE = r2.RCP_PHONE 
)
WHERE EXISTS (
    SELECT 1
	from recipient r
	where r.RCP_PHONE in (
		select r2.RCP_PHONE 
	    FROM recipient r2
	    WHERE r2.RCP_LOGIN IS NULL
	    GROUP BY r2.RCP_PHONE
	    HAVING COUNT(*) > 1
    )
    and tr.RCP_ID = r.RCP_ID 
    and r.RCP_LOGIN is NULL
    AND tr.MSG_ID
);

-- Then delete the other recipients
DELETE FROM recipient
WHERE RCP_LOGIN IS NULL
  AND RCP_ID NOT IN (SELECT RCP_ID FROM to_recipient)
  AND RCP_PHONE IN (
    SELECT RCP_PHONE
    FROM recipient
    WHERE RCP_LOGIN IS NULL
    GROUP BY RCP_PHONE
    HAVING COUNT(*) > 1
  );

-- Now the following 2 SQL commands must return the same result
/*
select count(DISTINCT RCP_PHONE) from recipient where RCP_LOGIN is null;
select count(RCP_PHONE) from recipient where RCP_LOGIN is null;
*/