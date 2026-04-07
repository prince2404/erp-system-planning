import { useMutation, useQueryClient } from '@tanstack/react-query';
import { endpoints } from '../api/endpoints.js';
import JsonAction from './JsonAction.jsx';
import SectionCard from './SectionCard.jsx';
import SmartForm from './SmartForm.jsx';

export default function ApiFormCard({ title, description, endpoint, fields, sample, method = 'post', invalidate = [], submitLabel = 'Submit' }) {
  const queryClient = useQueryClient();
  const mutation = useMutation({
    mutationFn: (payload) => {
      const resolvedEndpoint = typeof endpoint === 'function' ? endpoint(payload) : endpoint;
      if (method === 'put') {
        return endpoints.put(resolvedEndpoint, payload);
      }
      if (method === 'delete') {
        return endpoints.del(resolvedEndpoint);
      }
      return endpoints.post(resolvedEndpoint, payload);
    },
    onSuccess: () => invalidate.forEach((key) => queryClient.invalidateQueries({ queryKey: [key] }))
  });

  return (
    <SectionCard title={title} description={description}>
      {fields ? (
        <SmartForm fields={fields} submitLabel={submitLabel} busy={mutation.isPending} onSubmit={(payload) => mutation.mutateAsync(payload)} />
      ) : (
        <JsonAction label={submitLabel} sample={sample} busy={mutation.isPending} onSubmit={(payload) => mutation.mutateAsync(payload)} />
      )}
      {mutation.isSuccess ? <p className="mt-3 rounded-xl bg-emerald-50 px-3 py-2 text-sm font-medium text-emerald-700">Saved successfully.</p> : null}
      {mutation.isError ? <p className="mt-3 rounded-xl bg-red-50 px-3 py-2 text-sm font-medium text-red-700">{mutation.error.response?.data?.message || mutation.error.message}</p> : null}
    </SectionCard>
  );
}
