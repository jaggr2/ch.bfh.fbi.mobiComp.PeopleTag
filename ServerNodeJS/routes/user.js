
/*
 * GET userlist page.
 */

exports.userlist = function(db) {
    return function(req, res) {
        db.collection('userlist').find().toArray(function (err, items) {
            res.json(items);
        })
    }
};

/*
 * GET to get a user.
 */

exports.getuser = function(db) {
    return function(req, res) {
        var userToGet = req.params.id;
        db.collection('userlist').findById(userToGet, function(err, result) {
            res.send(result);
        });
    }
};


/*
 * POST to create a new user.
 */

exports.adduser = function(db) {
    return function(req, res) {
        req.body.positionReportedAt = new Date();
        req.body.createdAt = new Date();
        req.body.updatedAt = new Date();
        db.collection('userlist').insert(req.body, function(err, result){
            res.send(
                (err === null) ? { msg: '' } : { msg: err }
            );
        });
    }
};

/*
 * POST to update user.
 */

exports.updateuser = function(db) {
    return function(req, res) {
        var userToGet = req.params.id;
        db.collection('userlist').findById(userToGet, function(err, result) {

            if( err !== null) {
                res.send(404, { msg: 'User with ID ' + userToGet + ' not found!' });
                return;
            }

            if( req.body.displayName === undefined || req.body.displayName === null) {
                res.send(400, { msg: 'displayName can not be empty!' });
                return;
            }

            var updateStatement = { $set : { updatedAt: new Date() } };

            updateStatement.$set.displayName = req.body.displayName;

            if( req.body.currentLatitude !== undefined && req.body.currentLatitude !== null) {
                updateStatement.$set.currentLatitude = req.body.currentLatitude;
                updateStatement.$set.positionReportedAt = new Date();
            }

            if( req.body.currentLongitude !== undefined && req.body.currentLongitude !== null) {
                updateStatement.$set.currentLongitude = req.body.currentLongitude;
                updateStatement.$set.positionReportedAt = new Date();
            }

            db.collection('userlist').updateById(userToGet, updateStatement, { safe: true }, function(err, modifiedcount){

                res.send(
                    (err === null) ? { msg: modifiedcount } : { msg: err }
                );
            });

        });
    }
};


/*
 * DELETE to deleteuser.
 */

exports.deleteuser = function(db) {
    return function(req, res) {
        var userToDelete = req.params.id;
        db.collection('userlist').removeById(userToDelete, function(err, result) {
            res.send((result === 1) ? { msg: '' } : { msg:'error: ' + err });
        });
    }
};
