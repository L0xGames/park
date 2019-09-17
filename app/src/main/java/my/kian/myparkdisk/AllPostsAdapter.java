package my.kian.myparkdisk;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import my.kian.myparkdisk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllPostsAdapter extends RecyclerView.Adapter<AllPostsAdapter.ViewHolder> {
    Context context;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String uid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mPostsCollection = db.collection("posts");
    private Query mQuery= mPostsCollection;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mAuth.getCurrentUser()!=null){
            uid = mAuth.getCurrentUser().getUid();
        }
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View rowpostView = inflater.inflate(R.layout.item_post, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(rowpostView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Post post=mAllPosts.get(position);

        TextView plateView=holder.plateView;
        TextView descrView=holder.descrView;
        plateView.setText(post.plate);
        descrView.setText(post.describtion);


        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass datat to frag prepare
                Bundle bundle=new Bundle();
                bundle.putString("plate",mAllPosts.get(position).plate);
                bundle.putString("phone",mAllPosts.get(position).phone);
                bundle.putString("email",mAllPosts.get(position).email);
                bundle.putString("describtion",mAllPosts.get(position).describtion);
                Fragment fragment=new FullPostFragment();
                fragment.setArguments(bundle);
                //start fragment
                Context context=holder.plateView.getContext();
                FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.fragment_container,
                        fragment).addToBackStack( "tag" ).commit();
            }
        });

        if (holder.buttonOptions!=null){
            final Button button = holder.buttonOptions;

            holder.buttonOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(holder.buttonOptions.getContext(), button);

                    popup.inflate(R.menu.custom_menu);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_custom_delete:
                                    //get uid again
                                    if (mAuth.getCurrentUser()!=null){
                                        uid = mAuth.getCurrentUser().getUid();
                                    }
                                    new AlertDialog.Builder(holder.buttonOptions.getContext()).setTitle("Kennzeichen löschen").setMessage("Bist du sicher?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Post remove_post=mAllPosts.get(holder.getAdapterPosition());
                                                    //get post for removing
                                                    mQuery.whereEqualTo("username",remove_post.username).whereEqualTo("plate",remove_post.plate)
                                                            .whereEqualTo("phone",remove_post.phone).whereEqualTo("describtion",remove_post.describtion)
                                                            .whereEqualTo("email",remove_post.email)
                                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()){
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    //remove from db and adapter
                                                                    remove_db(document.getId(),holder.buttonOptions.getContext());
                                                                }
                                                            }
                                                            else{
                                                            }
                                                        }
                                                    });
                                                    mAllPosts.remove(holder.getAdapterPosition());
                                                    notifyItemRemoved(holder.getAdapterPosition());
                                                }
                                            }).setNegativeButton(android.R.string.no,null).setIcon(android.R.drawable.ic_dialog_alert).show();
                                    return true;
                            }
                            return false;
                        }
                    });

                    popup.show();
                }
            });
        }

    }

    private void remove_db(String doc_id, final Context context3) {
        //remove from db
        db.collection("posts").document(doc_id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context3,"Kennzeichen erfolgreich gelöscht!",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context3,"Kennzeichen konnte nicht gelöscht werden",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mAllPosts.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView plateView;
        private TextView descrView;
        private LinearLayout linearLayout;
        public Button buttonOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout=itemView.findViewById(R.id.linearLayout_item);
            plateView=itemView.findViewById(R.id.license_plate_textView);
            buttonOptions =itemView.findViewById(R.id.buttonOptions);
            descrView=itemView.findViewById(R.id.describtion_textView);
        }


    }
    private List<Post> mAllPosts,itemsCopy;

    public AllPostsAdapter(List<Post> mAllPosts) {
        this.mAllPosts = mAllPosts;
        this.itemsCopy=new ArrayList<>(mAllPosts);
    }
    public void filter(String text) {
        mAllPosts.clear();
        if(text.isEmpty()){
            mAllPosts.addAll(itemsCopy);
        } else{
            text = text.toLowerCase();
            for(Post item: itemsCopy){
                if(item.plate.toLowerCase().contains(text)){
                    mAllPosts.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

}
